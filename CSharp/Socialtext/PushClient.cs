using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Net;
using System.Web;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Json;
using System.Collections.Specialized;

namespace Socialtext
{
    [DataContract]
    class PushObject
    {
        private String fClass;
        private JsonDict fObjectDict;
        private Signal fSignal;

        [DataMember(Name = "class", IsRequired = true, Order = 1)]
        public String Class
        {
            get { return fClass; }
            set { fClass = value; }
        }

        [DataMember(Name = "object", IsRequired = true, Order = 2)]
        public JsonDict ObjectDict
        {
            get { return fObjectDict; }
            set { fObjectDict = value; Parse(); }
        }

        public Object Object
        {
            get
            {
                if (fClass == "signal") return fSignal;
                return null;
            }
        }

        public void Parse()
        {
            MemoryStream ms = new MemoryStream();
            DataContractJsonSerializer j = new DataContractJsonSerializer(typeof(JsonDict));
            j.WriteObject(ms, fObjectDict);
            ms.Flush();
            ms.Seek(0, SeekOrigin.Begin);

            if (fClass == "signal")
            {
                j = new DataContractJsonSerializer(typeof(Signal));
                fSignal = (Signal)j.ReadObject(ms);
            }
        }
    }

    [Serializable]
    public class JsonDict : ISerializable
    {
        public Dictionary<string, string> dict;
        public JsonDict()
        {
            dict = new Dictionary<string, string>();
        }
        protected JsonDict(SerializationInfo info, StreamingContext context)
        {
            dict = new Dictionary<string, string>();
            foreach (SerializationEntry entry in info)
            {
                dict.Add(entry.Name, entry.Value as String);
            }
        }
        public void GetObjectData(SerializationInfo info, StreamingContext context)
        {
            foreach (string key in dict.Keys)
            {
                info.AddValue(key, dict[key]);
            }
        }
    }

    public class PushClient
    {
        Cookie fUserCookie;
        String fURL;
        Boolean fConnected;
        HttpWebRequest fRequest;

        public delegate void SignalDelegate(Signal s);
        public event SignalDelegate OnSignalPosted;

        public delegate void GoodbyeDelegate(String reason, UInt64 reconnect);
        public event GoodbyeDelegate OnGoodbye;

        public PushClient(String host, String username, String password)
        {
            StringBuilder url = new StringBuilder(host);
            if (!host.EndsWith("/"))
            {
                url.Append("/");
            }

            fURL = url.ToString() + "data/push";

            url.Append("nlw/submit/login");

            Uri uri = new Uri(url.ToString(), UriKind.Absolute);
            fRequest = (HttpWebRequest)WebRequest.Create(uri);
            CookieContainer cc = new CookieContainer();

            fRequest.AllowAutoRedirect = false;
            fRequest.Method = "POST";
            fRequest.ContentType = "application/x-www-form-urlencoded";
            fRequest.CookieContainer = cc;

            String post = "username=" + HttpUtility.UrlEncode(ASCIIEncoding.ASCII.GetBytes(username));
            post += "&password=" + HttpUtility.UrlEncode(ASCIIEncoding.ASCII.GetBytes(password));
            post += "&remember=1";

            fRequest.ContentLength = ASCIIEncoding.ASCII.GetByteCount(post);
            Stream data = fRequest.GetRequestStream();
            data.Write(ASCIIEncoding.ASCII.GetBytes(post), 0, (int)fRequest.ContentLength);
            data.Close();

            HttpWebResponse response = (HttpWebResponse)fRequest.GetResponse();

            CookieCollection c = cc.GetCookies(uri);
            fUserCookie = c["NLW-user"];
            fConnected = true;

            if (fUserCookie == null)
            {
                fConnected = false;
                throw new Exception("Did not find cookie");
            }
        }

        public void Shutdown()
        {
            fConnected = false;
            if (fRequest != null)
            {
                fRequest.Abort();
            }
        }

        public void Run()
        {
            bool nowait = true;
            int sequence = 1;
            String client_id = String.Empty;

            while (fConnected)
            {
                Uri uri;
                if (nowait)
                {
                    /* Do not wait the first time through */
                    uri = new Uri(fURL + "?nowait=1", UriKind.Absolute);
                    nowait = false;
                }
                else
                {
                    uri = new Uri(fURL + "?client_id=" + client_id + ";sequence=" + sequence, UriKind.Absolute);
                }
                fRequest = (HttpWebRequest)WebRequest.Create(uri);

                fRequest.Accept = "application/json";
                fRequest.CookieContainer = new CookieContainer();
                fRequest.CookieContainer.Add(fUserCookie);

                HttpWebResponse response = (HttpWebResponse)fRequest.GetResponse();

                DataContractJsonSerializer json = new DataContractJsonSerializer(typeof(PushObject[]));
                PushObject[] json_dict = (PushObject[])json.ReadObject(response.GetResponseStream());

                if (!fConnected)
                    return;

                foreach (PushObject po in json_dict)
                {
                    if (po.Class == "signal")
                    {
                        OnSignalPosted((Signal)po.Object);
                    }
                    else if (po.Class == "command")
                    {
                        Dictionary<String, String> dict = po.ObjectDict.dict;
                        if (dict["command"] == "goodbye") {
                            String reason = dict["reason"];
                            UInt64 timeout = UInt64.Parse(dict["reconnect_after"]);
                            OnGoodbye(reason, timeout);
                            fConnected = false;
                        }
                        else if (dict["command"] == "welcome")
                        {
                            client_id = dict["client_id"];
                        }
                        else if (dict["command"] == "continue")
                        {
                            Int32.TryParse(dict["sequence"], out sequence);
                        }
                    }
                }
            }
        }
    }
}

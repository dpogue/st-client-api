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
	/// <summary>
	/// A response from a Push request.
	/// </summary>
    [DataContract]
    class PushObject
    {
        private String fClass;
        private JsonDict fObjectDict;
        private Signal fSignal;

		/// <summary>
		/// The type of object contained in this response.
		/// </summary>
        [DataMember(Name = "class", IsRequired = true, Order = 1)]
        public String Class
        {
            get { return fClass; }
            set { fClass = value; }
        }

		/// <summary>
		/// The object in this response, as a JsonDict.
		/// </summary>
        [DataMember(Name = "object", IsRequired = true, Order = 2)]
        public JsonDict ObjectDict
        {
            get { return fObjectDict; }
            set { fObjectDict = value; Parse(); }
        }

		/// <summary>
		/// The object in this response, as an Object.
		/// </summary>
        public Object Object
        {
            get
            {
                if (fClass == "signal") return fSignal;
                return null;
            }
        }

		/// <summary>
		/// Populate the response object from the JsonDict.
		/// </summary>
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

	/// <summary>
	/// Wrapper for a standard C# Dictionary<T, U> so that is can be deserialized from JSON.
	/// </summary>
    [Serializable]
    public class JsonDict : ISerializable
    {
		/// <summary>
		/// The contents as a Dictionary<String, String>.
		/// </summary>
        public Dictionary<string, string> dict;
		
		/// <summary>
		/// Creates a JsonDict wrapper.
		/// </summary>
        public JsonDict()
        {
            dict = new Dictionary<string, string>();
        }
		
		/// <summary>
		/// Creates a JsonDict wrapper.
		/// </summary>
		/// <param name="info">
		/// The serialization information.
		/// </param>
		/// <param name="context">
		/// The streaming content.
		/// </param>
        protected JsonDict(SerializationInfo info, StreamingContext context)
        {
            dict = new Dictionary<string, string>();
            foreach (SerializationEntry entry in info)
            {
                dict.Add(entry.Name, entry.Value as String);
            }
        }
		
		/// <summary>
		/// Serialize the dictionary keys and values.
		/// </summary>
		/// <param name="info">
		/// The serialization information.
		/// </param>
		/// <param name="context">
		/// The streaming context.
		/// </param>
        public void GetObjectData(SerializationInfo info, StreamingContext context)
        {
            foreach (string key in dict.Keys)
            {
                info.AddValue(key, dict[key]);
            }
        }
    }

	/// <summary>
	/// The Socialtext Push Client.
	/// </summary>
    public class PushClient
    {
        private Cookie fUserCookie;
        private String fURL;
        private Boolean fConnected;
        private HttpWebRequest fRequest;

        public delegate void SignalDelegate(Signal s);
		/// <summary>
		/// Triggered when a Signal is received.
		/// </summary>
        public event SignalDelegate OnSignalPosted;

        public delegate void GoodbyeDelegate(String reason, UInt64 reconnect);
        /// <summary>
        /// Triggered when the server is shutting down the connection.
        /// </summary>
        public event GoodbyeDelegate OnGoodbye;

		/// <summary>
		/// Creates a new Push client.
		/// </summary>
		/// <param name="host">
		/// The Socialtext server address.
		/// </param>
		/// <param name="username">
		/// The username used to access Socialtext.
		/// </param>
		/// <param name="password">
		/// The password used to access Socialtext.
		/// </param>
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

		/// <summary>
		/// Closes the Push client connection.
		/// </summary
		/// <remarks>
		/// This doesn't seem to actually terminate an open connection.
		/// This can lead to lingering connections after an app is closed!
		/// </remarks>
        public void Shutdown()
        {
            fConnected = false;
            if (fRequest != null)
            {
                fRequest.Abort();
            }
        }

		/// <summary>
		/// Opens and runs a Push connection to receive data.
		/// </summary>
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

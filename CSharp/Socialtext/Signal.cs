using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Json;
using System.IO;
using System.Net;

namespace Socialtext
{
    [DataContract]
    public class Signal
    {
        private String fBody;
        private DateTime fDate;
        private Int32 fSignalID;
        private String fURI;
        private String fHash;
        private Person fPerson;

        [DataMember(Name="body")]
        public String Body
        {
            get { return fBody; }
            set { fBody = value; }
        }

        [DataMember(Name="signal_id")]
        public Int32 SignalID
        {
            get { return fSignalID; }
            set { fSignalID = value; }
        }

        [DataMember(Name = "uri", IsRequired = false, EmitDefaultValue = false)]
        public String URI
        {
            get { return fURI; }
            set { fURI = value; }
        }

        [DataMember(Name = "hash", IsRequired = false, EmitDefaultValue = false)]
        public String Hash
        {
            get { return fHash; }
            set { fHash = value; }
        }

        [DataMember(Name = "user_id", IsRequired = false, EmitDefaultValue = false)]
        public UInt32 UserID
        {
            get { return (fPerson == null) ? 0 : fPerson.PersonID; }
            set {
                if (fPerson == null)
                {
                    fPerson = new Person();
                }
                fPerson.PersonID = value;
            }
        }

        [DataMember(Name = "best_full_name", IsRequired = false, EmitDefaultValue = false)]
        public String BestFullName
        {
            get { return (fPerson == null) ? null : fPerson.BestFullName; }
            set {
                if (fPerson == null)
                {
                    fPerson = new Person();
                }
                fPerson.BestFullName = value;
            }
        }

        public static List<Signal> Get(RestClient rest)
        {
            return Get(rest, String.Empty);
        }

        public static List<Signal> Get(RestClient rest, String criteria)
        {
            StringBuilder url = new StringBuilder(rest.Host);
            if (!rest.Host.EndsWith("/"))
            {
                url.Append("/");
            }

            url.Append("data/signals");
            if (!String.IsNullOrWhiteSpace(criteria))
            {
                url.Append("?" + criteria);
            }

            Uri uri = new Uri(url.ToString(), UriKind.Absolute);
            HttpWebRequest req = (HttpWebRequest)WebRequest.Create(uri);

            req.Accept = "application/json";
            req.Headers["Authorization"] = "Basic " + Convert.ToBase64String(
                    Encoding.UTF8.GetBytes(rest.Username + ":" + rest.Password));

            HttpWebResponse response = (HttpWebResponse)req.GetResponse();

            DataContractJsonSerializer json = new DataContractJsonSerializer(typeof(Signal[]));
            Signal[] signals = (Signal[])json.ReadObject(response.GetResponseStream());

            return new List<Signal>(signals);
        }
    }
}

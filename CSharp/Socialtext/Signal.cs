using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Json;
using System.IO;
using System.Net;

namespace Socialtext
{
	/// <summary>
	/// A Socialtext Signal.
	/// </summary>
    [DataContract]
    public class Signal
    {
        private String fBody;
        private DateTime fDate = DateTime.Now;
        private Int32 fSignalID;
        private String fURI;
        private String fHash;
        private Person fPerson;
        private Signal fInReplyTo;

		/// <summary>
		/// The textual content of the Signal.
		/// </summary>
        [DataMember(Name="body", IsRequired = false)]
        public String Body
        {
            get { return fBody; }
            set { fBody = value; }
        }

		/// <summary>
		/// The unique identification number for this Signal.
		/// </summary>
        [DataMember(Name="signal_id")]
        public Int32 SignalID
        {
            get { return fSignalID; }
            set { fSignalID = value; }
        }

		/// <summary>
		/// The URI that links to this specific Signal.
		/// </summary>
        [DataMember(Name = "uri", IsRequired = false, EmitDefaultValue = false)]
        public String URI
        {
            get { return fURI; }
            set { fURI = value; }
        }

		/// <summary>
		/// A hash of this Signal object, suitable for use in a hashtable.
		/// </summary>
        [DataMember(Name = "hash", IsRequired = false, EmitDefaultValue = false)]
        public String Hash
        {
            get { return fHash; }
            set { fHash = value; }
        }

		/// <summary>
		/// The identifier of the Person who posted the Signal.
		/// </summary>
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

		/// <summary>
		/// The full name of the Person who posted the Signal.
		/// </summary>
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

		/// <summary>
		/// The Signal object to which the Signal is a reply.
		/// </summary>
        [DataMember(Name = "in_reply_to", IsRequired = false, EmitDefaultValue = false)]
        public Signal InReplyTo
        {
            get { return fInReplyTo; }
            set { fInReplyTo = value; }
        }

		/// <summary>
		/// The date & time of the Signal posting, as a String.
		/// </summary>
        [DataMember(Name = "at", IsRequired = false, EmitDefaultValue = true)]
        public String At
        {
            get { return fDate.ToLongTimeString(); }
            set { fDate = DateTime.Parse(value); }
        }

		/// <summary>
		/// The date & time of the Signal posting, as a DateTime.
		/// </summary>
        public DateTime Date
        {
            get { return fDate; }
            set { fDate = value; }
        }

		/// <summary>
		/// The Person who posted the Signal.
		/// </summary>
        public Person Person
        {
            get { return fPerson; }
            set { fPerson = value; }
        }

		/// <summary>
		/// Fetches the most recent Signals using ReST.
		/// </summary>
		/// <param name="rest">
		/// A <see cref="RestClient"/> connection used to fetch the Signals.
		/// </param>
		/// <returns>
		/// A <see cref="List<Signal>"/> containing the most recent Signals.
		/// </returns>
        public static List<Signal> Get(RestClient rest)
        {
            return Get(rest, String.Empty);
        }

		/// <summary>
		/// Fetches Signals matching criteria using ReST.
		/// </summary>
		/// <param name="rest">
		/// A <see cref="RestClient"/> connection used to fetch the Signals.
		/// </param>
		/// <param name="criteria">
		/// The criteria to be used when fetching Signals.
		/// </param>
		/// <returns>
		/// A <see cref="List<Signal>"/> containing the returned Signals.
		/// </returns>
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

            foreach (Signal s in signals)
            {
                s.Person.GetPhoto(rest);
            }

            return new List<Signal>(signals);
        }
    }
}

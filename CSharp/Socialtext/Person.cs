using System;
using System.Drawing;
using System.Collections.Generic;
using System.Text;
using System.Net;
using System.IO;
using System.Collections;

namespace Socialtext
{
    public class Person
    {
        private UInt32 fPersonID;
        private String fBestFullName;

        private static readonly object sync = new object();
        private static Dictionary<UInt32, Image> fPhotos = new Dictionary<UInt32, Image>(); 

        public UInt32 PersonID
        {
            get { return fPersonID; }
            set { fPersonID = value; }
        }

        public String BestFullName
        {
            get { return fBestFullName; }
            set { fBestFullName = value; }
        }

        public Image GetPhoto(RestClient rest)
        {
            Image ret = null;

            lock (sync)
            {
                if (fPhotos.ContainsKey(fPersonID))
                {
                    ret = fPhotos[fPersonID];
                }
            }

            if (ret != null)
            {
                return ret;
            }

            StringBuilder url = new StringBuilder(rest.Host);
            if (!rest.Host.EndsWith("/"))
            {
                url.Append("/");
            }

            url.Append("data/people/");
            url.Append(fPersonID);
            url.Append("/photo");

            Uri uri = new Uri(url.ToString(), UriKind.Absolute);
            HttpWebRequest req = (HttpWebRequest)WebRequest.Create(uri);

            req.Accept = "image/png";
            req.Headers["Authorization"] = "Basic " + Convert.ToBase64String(
                    Encoding.UTF8.GetBytes(rest.Username + ":" + rest.Password));

            try
            {
                HttpWebResponse response = (HttpWebResponse)req.GetResponse();

                lock (sync)
                {
                    fPhotos.Add(fPersonID, Image.FromStream(response.GetResponseStream()));
                    ret = fPhotos[fPersonID];
                }

                return ret;
            }
            catch
            {
                return null;
            }
        }
    }
}
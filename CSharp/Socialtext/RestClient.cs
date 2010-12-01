using System;
using System.Collections.Generic;
using System.Text;

namespace Socialtext
{
    public class RestClient
    {
        private String fHost;
        private String fUsername;
        private String fPassword;

        public String Host
        {
            get { return fHost; }
            set { fHost = (String.IsNullOrWhiteSpace(value) ? fHost : value); }
        }

        public String Username
        {
            get { return fUsername; }
            set { fUsername = (String.IsNullOrWhiteSpace(value) ? fUsername : value); }
        }

        public String Password
        {
            internal get { return fPassword; }
            set { fPassword = (String.IsNullOrWhiteSpace(value) ? fPassword : value); }
        }

        public RestClient(String host)
        {
            fHost = host;
        }

        public RestClient(String host, String username, String password)
        {
            fHost = host;
            fUsername = username;
            fPassword = password;
        }

        public PushClient GetPushClient()
        {
            return new PushClient(fHost, fUsername, fPassword);
        }
    }
}

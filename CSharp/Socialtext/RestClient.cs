using System;
using System.Collections.Generic;
using System.Text;

namespace Socialtext
{
	/// <summary>
	/// The Socialtext ReST Connection.
	/// </summary>
    public class RestClient
    {
        private String fHost;
        private String fUsername;
        private String fPassword;

		/// <summary>
		/// The Socialtext server address.
		/// </summary>
        public String Host
        {
            get { return fHost; }
            set { fHost = (String.IsNullOrWhiteSpace(value) ? fHost : value); }
        }

		/// <summary>
		/// The username used to access Socialtext.
		/// </summary>
        public String Username
        {
            get { return fUsername; }
            set { fUsername = (String.IsNullOrWhiteSpace(value) ? fUsername : value); }
        }

		/// <summary>
		/// The password used to access Socialtext.
		/// </summary>
        public String Password
        {
            internal get { return fPassword; }
            set { fPassword = (String.IsNullOrWhiteSpace(value) ? fPassword : value); }
        }

		/// <summary>
		/// Creates a new ReST client object.
		/// </summary>
		/// <param name="host">
		/// The Socialtext server address.
		/// </param>
        public RestClient(String host)
        {
            fHost = host;
        }

		/// <summary>
		/// Creates a new ReST client object.
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
        public RestClient(String host, String username, String password)
        {
            fHost = host;
            fUsername = username;
            fPassword = password;
        }

		/// <summary>
		/// Creates and returns a <see cref="PushClient"/> with the given host and user details.
		/// </summary>
		/// <returns>
		/// A <see cref="PushClient"/> with the given host and user details.
		/// </returns>
        public PushClient GetPushClient()
        {
            return new PushClient(fHost, fUsername, fPassword);
        }
    }
}

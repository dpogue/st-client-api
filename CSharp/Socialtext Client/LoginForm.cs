using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace Socialtext.Client
{
    public partial class LoginForm : Form
    {
        private String fHost;
        private String fUsername;
        private String fPassword;
        private Boolean fRemember;

        public LoginForm()
        {
            InitializeComponent();

            txtHost.Text = fHost;
            txtUsername.Text = fUsername;
            txtPassword.Text = fPassword;
            chkRemember.Checked = fRemember;
        }

        private void btnConnect_Click(object sender, EventArgs e)
        {
            fHost = txtHost.Text;
            fUsername = txtUsername.Text;
            fPassword = txtPassword.Text;
            fRemember = chkRemember.Checked;

            if (!String.IsNullOrWhiteSpace(fHost)
                && !String.IsNullOrWhiteSpace(fUsername)
                && !String.IsNullOrWhiteSpace(fPassword))
            {
                SignalsForm.Client = new RestClient(fHost, fUsername, fPassword);
                DialogResult = System.Windows.Forms.DialogResult.OK;
            }
            else
            {
                DialogResult = System.Windows.Forms.DialogResult.Retry;
            }

            this.Close();
        }
    }
}

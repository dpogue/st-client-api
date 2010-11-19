using System;
using System.Collections.Generic;
using System.Text;

namespace Socialtext
{
    public class Person
    {
        private UInt32 fPersonID;
        private String fBestFullName;
        private String fSortKey;

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
    }
}

import com.socialtext.Resting;
import com.socialtext.Signal;
import java.io.Console;
import java.util.ArrayList;

public class PostSignal
{
    public static void main(String[] args)
    {
        new PostSignal();
    }

    private Resting m_client;

    public PostSignal()
    {
        Console con;

        if ((con = System.console()) != null)
        {
            try
            {
                String url = con.readLine("%s: ", "Socialtext URL");
                String login = con.readLine("%s: ", "Username");
                char[] passwd = con.readPassword("%s: ", "Password");

                m_client = new Resting(url, login, new String(passwd));
                java.util.Arrays.fill(passwd, ' ');
                
                System.out.println();
                
                String sig = con.readLine("%s: ", "Signal text");
                
                Signal signal = new Signal();
                signal.setBody(sig);
                m_client.postSignal(signal);
            }
            catch (IllegalArgumentException iae)
            {
                iae.printStackTrace();
                return;
            }
        }

        if (m_client == null) {
            System.out.println("Unable to create client.");
            return;
        }
    }
}

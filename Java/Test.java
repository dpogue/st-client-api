import com.socialtext.Resting;
import com.socialtext.Signal;

public class Test
{
    public static void main(String[] args)
    {
        String url = "https://developers.socialtext.net";
        String username = args[0];
        String password = args[1];
        
        Resting r = new Resting(url, username, password);
        
        Signal[] sigs = r.getSignals();

        for (int i = 0; i < sigs.length; i++) {
            System.out.println(sigs[i]);
        }

        // Testing a post
        Signal dox = new Signal();
        dox.setBody("Sending a signal from Java...");
        r.postSignal(dox);
    }
}

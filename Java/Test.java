import com.socialtext.Resting;
import com.socialtext.Signal;
import com.socialtext.People;
import java.util.ArrayList;

public class Test
{
    public static void main(String[] args)
    {
        String url = "https://developers.socialtext.net";
        String username = args[0];
        String password = args[1];
        
        Resting r = new Resting(url, username, password);
        
	    ArrayList<Signal> sigs = r.getSignals();

        for (int i = 0; i < sigs.size(); i++) {
            System.out.println(sigs.get(i));
        }
        
        //Get all people the user can see
	    ArrayList<People> peops = r.getPeople();

        for (int i = 0; i < peops.size(); i++) {
            System.out.println(peops.get(i));
        }

        // Testing a post
        //Signal dox = new Signal();
        //dox.setBody("Sending a signal from Java...");
        //r.postSignal(dox);
    }
}

import com.socialtext.Resting;

public class Test
{
    public static void main(String[] args)
    {
        String url = "https://developers.socialtext.net";
        String username = "";
        String password = "";
        
        Resting r = new Resting(url, username, password);
        
        r.getSignals();
    }
}

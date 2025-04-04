package exercise5;

/**
 * @author Hedy Huang
 * @version 1.0
 */
public class SocialMediaApp {
    public static void main(String[] args) {
        // 1. Factory: Create users
        System.out.println("Create users:");
        User taylor = UserFactory.createUser("influencer", "Taylor");
        User hedy = UserFactory.createUser("regular", "Hedy");
        taylor.displayRole();
        hedy.displayRole();

        // 2. Observer: Hedy follows Taylor
        taylor.addFollower(new SocialMediaFollower(hedy.username));

        // 3. Decorator: Taylor posts a decorated update
        Post post = new BasicPost("Released a new song");
        post = new EmojiDecorator(post, "\uD83C\uDFB6");
        post = new HashtagDecorator(post,"#Taylor Swift");

        taylor.postUpdate(post);
    }
}

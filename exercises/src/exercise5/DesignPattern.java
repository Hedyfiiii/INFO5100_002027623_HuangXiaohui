package exercise5;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hedy Huang
 * @version 1.0
 */

// Here is a realistic social media system demonstrating three design patterns:

// 1. Factory Pattern (User Creation)
abstract class User {
    protected String username;
    protected List<Follower> followers = new ArrayList<>();

    public User(String username) {
        this.username = username;
    }

    public abstract void displayRole();

    public void addFollower(Follower follower) {
        followers.add(follower);
        System.out.println("\n" + follower.getName() + " started following " + username);
    }

    public void postUpdate(Post post) {
        System.out.println("\n" + username + " posted: " + post.getContent());
        notifyFollowers(post);
    }

    private void notifyFollowers(Post post) {
        for (Follower follower : followers) {
            follower.update(this, post);
        }
    }
}

class RegularUser extends User {
    public RegularUser(String username) {
        super(username);
    }

    @Override
    public void displayRole() {
        System.out.println(username + " (Regular User)");
    }
}

class InfluencerUser extends User {
    public InfluencerUser(String username) {
        super(username);
    }

    @Override
    public void displayRole() {
        System.out.println(username + " (Influencer)");
    }
}

class UserFactory {
    public static User createUser(String type, String username) {
        if (type.equalsIgnoreCase("influencer")){
            return new InfluencerUser(username);
        }else {
            return new RegularUser(username);
        }
    }
}

// 2. Observer Pattern (Notifications)
interface Follower {
    void update(User user, Post post);
    String getName();
}

class SocialMediaFollower implements Follower {
    private final String name;

    public SocialMediaFollower(String name) {
        this.name = name;
    }

    @Override
    public void update(User user, Post post) {
        System.out.println(name + " received notification: " +
                user.username + " posted: " + post.getContent());
    }

    @Override
    public String getName() {
        return name;
    }
}

// 3. Decorator Pattern (Post Enhancements)
interface Post {
    String getContent();
}

class BasicPost implements Post {
    private final String content;

    public BasicPost(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }
}

abstract class PostDecorator implements Post {
    protected Post decoratedPost;

    public PostDecorator(Post decoratedPost) {
        this.decoratedPost = decoratedPost;
    }

    @Override
    public String getContent() {
        return decoratedPost.getContent();
    }
}

class EmojiDecorator extends PostDecorator {
    private final String emoji;

    public EmojiDecorator(Post decoratedPost, String emoji) {
        super(decoratedPost);
        this.emoji = emoji;
    }

    @Override
    public String getContent() {
        return super.getContent() + " " + emoji;
    }
}

class HashtagDecorator extends PostDecorator {
    private final String hashtag;

    public HashtagDecorator(Post decoratedPost, String hashtag) {
        super(decoratedPost);
        this.hashtag = hashtag;
    }

    @Override
    public String getContent() {
        return super.getContent() + " " + hashtag;
    }
}

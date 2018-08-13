package com.tts.farmer;

public class ChatList extends pushid{
    String profilepic,profilepicthumb;
    String name;


    public ChatList(String profilepic, String profilepicthumb, String name) {
        this.profilepic = profilepic;
        this.profilepicthumb = profilepicthumb;
        this.name = name;
    }

    public String getProfilepicthumb() {
        return profilepicthumb;

    }

    public String getProfilepic() {
        return profilepic;
    }

    public ChatList() {
    }

    public String getName() {
        return name;
    }



}

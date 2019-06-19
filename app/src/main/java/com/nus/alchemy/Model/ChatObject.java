package com.nus.alchemy.Model;

public class ChatObject {

    String chatID;
    String title;
    String otherUserID;
    String otherUserName;

    public ChatObject(String chatID, String otherUserID) {
        this.chatID = chatID;
        this.otherUserID = otherUserID;
    }

    public String getChatID() { return chatID; }

    public String getOtherUserID() {
        return otherUserID;
    }

//    public String getOtherUserName() {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(otherUserID);
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    otherUserName = dataSnapshot.child("Name").getValue().toString();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//        ref.child("Helper").setValue(false);
//        ref.child("Helper").setValue(true);
//        return otherUserName;
//    }

}

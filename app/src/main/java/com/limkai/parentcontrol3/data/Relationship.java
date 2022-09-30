package com.limkai.parentcontrol3.data;

public class Relationship {

    private String ParentEmail;
    private String ChildEmail;
    private String ParentName;
    private String ChildName;
    private String RelationshipId;

    public Relationship() {

    }

    public Relationship(String parentEmail, String childEmail, String parentName, String childName, String relationshipId) {
        ParentEmail = parentEmail;
        ChildEmail = childEmail;
        ParentName = parentName;
        ChildName = childName;
        RelationshipId = relationshipId;
    }

    public String getParentEmail() {
        return ParentEmail;
    }

    public void setParentEmail(String parentEmail) {
        ParentEmail = parentEmail;
    }

    public String getChildEmail() {
        return ChildEmail;
    }

    public void setChildEmail(String childEmail) {
        ChildEmail = childEmail;
    }

    public String getParent() {
        return ParentName;
    }

    public void setParent(String parent) {
        ParentName = parent;
    }

    public String getChild() {
        return ChildName;
    }

    public void setChild(String child) {
        ChildName = child;
    }

    public String getRelationshipId() {
        return RelationshipId;
    }

    public void setRelationshipId(String relationshipId) {
        RelationshipId = relationshipId;
    }

    @Override
    public String toString() {
        return ChildName;
    }
}

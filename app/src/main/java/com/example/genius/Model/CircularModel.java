package com.example.genius.Model;

import java.util.List;

public class CircularModel {

    public String Message;
    public Boolean Completed;
    public List<CircularData> Data;

    public static class CircularData {
        public long CircularId;
        public String CircularTitle;
        public String CircularDescription;
        public String FilePath;
        public String FileName;
    }
}

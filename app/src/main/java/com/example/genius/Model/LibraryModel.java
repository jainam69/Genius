package com.example.genius.Model;

public class LibraryModel {

    long LibraryID;
    long BranchID;
    String ThumbImageName;
    String ThumbDocName;
    int Type;
    long StandardID;
    long SubjectID;
    String Description;
    RowStatusModel RowStatus;
    TransactionModel Transaction;
    LibraryDataEntity LibraryData;

    public LibraryModel(long libraryID, long branchID, String thumbImageName, String thumbDocName, int type, long standardID, long subjectID, String description, RowStatusModel rowStatus, TransactionModel transaction, LibraryDataEntity libraryData) {
        LibraryID = libraryID;
        BranchID = branchID;
        ThumbImageName = thumbImageName;
        ThumbDocName = thumbDocName;
        Type = type;
        StandardID = standardID;
        SubjectID = subjectID;
        Description = description;
        RowStatus = rowStatus;
        Transaction = transaction;
        LibraryData = libraryData;
    }

    public LibraryModel(long branchID, String thumbImageName, String thumbDocName, int type, long standardID, long subjectID, String description, RowStatusModel rowStatus, TransactionModel transaction, LibraryDataEntity libraryData) {
        BranchID = branchID;
        ThumbImageName = thumbImageName;
        ThumbDocName = thumbDocName;
        Type = type;
        StandardID = standardID;
        SubjectID = subjectID;
        Description = description;
        RowStatus = rowStatus;
        Transaction = transaction;
        LibraryData = libraryData;
    }

    public long getLibraryID() {
        return LibraryID;
    }

    public void setLibraryID(long libraryID) {
        LibraryID = libraryID;
    }

    public long getBranchID() {
        return BranchID;
    }

    public void setBranchID(long branchID) {
        BranchID = branchID;
    }

    public String getThumbImageName() {
        return ThumbImageName;
    }

    public void setThumbImageName(String thumbImageName) {
        ThumbImageName = thumbImageName;
    }

    public String getThumbDocName() {
        return ThumbDocName;
    }

    public void setThumbDocName(String thumbDocName) {
        ThumbDocName = thumbDocName;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public long getStandardID() {
        return StandardID;
    }

    public void setStandardID(long standardID) {
        StandardID = standardID;
    }

    public long getSubjectID() {
        return SubjectID;
    }

    public void setSubjectID(long subjectID) {
        SubjectID = subjectID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public RowStatusModel getRowStatus() {
        return RowStatus;
    }

    public void setRowStatus(RowStatusModel rowStatus) {
        RowStatus = rowStatus;
    }

    public TransactionModel getTransaction() {
        return Transaction;
    }

    public void setTransaction(TransactionModel transaction) {
        Transaction = transaction;
    }

    public LibraryDataEntity getLibraryData() {
        return LibraryData;
    }

    public void setLibraryData(LibraryDataEntity libraryData) {
        LibraryData = libraryData;
    }

    public static class LibraryDataEntity{

        long UniqueID;
        long LibraryID;
        String ThumbImageContentText;
        String ThumbImageExt;
        String ThumbImageFileName;
        String DocContentText;
        String DocContentFileName;
        String DocContentExt;

        public LibraryDataEntity(long uniqueID, long libraryID, String thumbImageContentText, String thumbImageExt, String thumbImageFileName, String docContentText, String docContentFileName, String docContentExt) {
            UniqueID = uniqueID;
            LibraryID = libraryID;
            ThumbImageContentText = thumbImageContentText;
            ThumbImageExt = thumbImageExt;
            ThumbImageFileName = thumbImageFileName;
            DocContentText = docContentText;
            DocContentFileName = docContentFileName;
            DocContentExt = docContentExt;
        }

        public LibraryDataEntity(String thumbImageContentText, String thumbImageExt, String thumbImageFileName, String docContentText, String docContentFileName, String docContentExt) {
            ThumbImageContentText = thumbImageContentText;
            ThumbImageExt = thumbImageExt;
            ThumbImageFileName = thumbImageFileName;
            DocContentText = docContentText;
            DocContentFileName = docContentFileName;
            DocContentExt = docContentExt;
        }

        public long getUniqueID() {
            return UniqueID;
        }

        public void setUniqueID(long uniqueID) {
            UniqueID = uniqueID;
        }

        public long getLibraryID() {
            return LibraryID;
        }

        public void setLibraryID(long libraryID) {
            LibraryID = libraryID;
        }

        public String getThumbImageContentText() {
            return ThumbImageContentText;
        }

        public void setThumbImageContentText(String thumbImageContentText) {
            ThumbImageContentText = thumbImageContentText;
        }

        public String getThumbImageExt() {
            return ThumbImageExt;
        }

        public void setThumbImageExt(String thumbImageExt) {
            ThumbImageExt = thumbImageExt;
        }

        public String getThumbImageFileName() {
            return ThumbImageFileName;
        }

        public void setThumbImageFileName(String thumbImageFileName) {
            ThumbImageFileName = thumbImageFileName;
        }

        public String getDocContentText() {
            return DocContentText;
        }

        public void setDocContentText(String docContentText) {
            DocContentText = docContentText;
        }

        public String getDocContentFileName() {
            return DocContentFileName;
        }

        public void setDocContentFileName(String docContentFileName) {
            DocContentFileName = docContentFileName;
        }

        public String getDocContentExt() {
            return DocContentExt;
        }

        public void setDocContentExt(String docContentExt) {
            DocContentExt = docContentExt;
        }
    }

    public static class LibraryData1{

        boolean Completed;
        LibraryModel Data;

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public LibraryModel getData() {
            return Data;
        }

        public void setData(LibraryModel data) {
            Data = data;
        }
    }
}

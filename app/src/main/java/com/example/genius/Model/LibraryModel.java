package com.example.genius.Model;

public class LibraryModel {

    long LibraryID;
    long LibraryDetailID;
    long Type;
    public String Title;
    public byte[] FileContent;
    public String link;
    public String FileName;
    public String FilePath;
    public String Description;
    public RowStatusModel RowStatus;
    public TransactionModel Transaction;
    public BranchModel BranchInfo;
    public CategoryModel CategoryInfo;

    public LibraryModel(long libraryID, long libraryDetailID, long type, String title, byte[] fileContent, String link, String fileName, String filePath, String description, RowStatusModel rowStatus, TransactionModel transaction, BranchModel branchInfo, CategoryModel categoryInfo) {
        LibraryID = libraryID;
        LibraryDetailID = libraryDetailID;
        Type = type;
        Title = title;
        FileContent = fileContent;
        this.link = link;
        FileName = fileName;
        FilePath = filePath;
        Description = description;
        RowStatus = rowStatus;
        Transaction = transaction;
        BranchInfo = branchInfo;
        CategoryInfo = categoryInfo;
    }

    public long getLibraryID() {
        return LibraryID;
    }

    public void setLibraryID(long libraryID) {
        LibraryID = libraryID;
    }

    public long getLibraryDetailID() {
        return LibraryDetailID;
    }

    public void setLibraryDetailID(long libraryDetailID) {
        LibraryDetailID = libraryDetailID;
    }

    public long getType() {
        return Type;
    }

    public void setType(long type) {
        Type = type;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public byte[] getFileContent() {
        return FileContent;
    }

    public void setFileContent(byte[] fileContent) {
        FileContent = fileContent;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
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

    public BranchModel getBranchInfo() {
        return BranchInfo;
    }

    public void setBranchInfo(BranchModel branchInfo) {
        BranchInfo = branchInfo;
    }

    public CategoryModel getCategoryInfo() {
        return CategoryInfo;
    }

    public void setCategoryInfo(CategoryModel categoryInfo) {
        CategoryInfo = categoryInfo;
    }

}

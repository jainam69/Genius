package com.example.genius.Model;

import java.util.List;

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
    public String VideoLink;
    public String LibraryTitle;
    public String ThumbnailFileName;
    public String ThumbnailFilePath;
    public String DocFileName;
    public String DocFilePath;
    public ApprovalModel approval;
    public List<SubjectModel> Subjectlist;
    public List<LibraryStandardModel> list;
    public long BranchID;
    public LibraryModel(long libraryID) {
        LibraryID = libraryID;
    }

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

    public long getBranchID() {
        return BranchID;
    }

    public void setBranchID(long branchID) {
        BranchID = branchID;
    }

    public List<SubjectModel> getSubjectlist() {
        return Subjectlist;
    }

    public void setSubjectlist(List<SubjectModel> subjectlist) {
        Subjectlist = subjectlist;
    }

    public String getVideoLink() {
        return VideoLink;
    }

    public void setVideoLink(String videoLink) {
        VideoLink = videoLink;
    }

    public String getLibraryTitle() {
        return LibraryTitle;
    }

    public void setLibraryTitle(String libraryTitle) {
        LibraryTitle = libraryTitle;
    }

    public String getThumbnailFileName() {
        return ThumbnailFileName;
    }

    public void setThumbnailFileName(String thumbnailFileName) {
        ThumbnailFileName = thumbnailFileName;
    }

    public String getThumbnailFilePath() {
        return ThumbnailFilePath;
    }

    public void setThumbnailFilePath(String thumbnailFilePath) {
        ThumbnailFilePath = thumbnailFilePath;
    }

    public String getDocFileName() {
        return DocFileName;
    }

    public void setDocFileName(String docFileName) {
        DocFileName = docFileName;
    }

    public String getDocFilePath() {
        return DocFilePath;
    }

    public void setDocFilePath(String docFilePath) {
        DocFilePath = docFilePath;
    }

    public ApprovalModel getApproval() {
        return approval;
    }

    public void setApproval(ApprovalModel approval) {
        this.approval = approval;
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

    public List<LibraryStandardModel> getList() {
        return list;
    }

    public void setList(List<LibraryStandardModel> list) {
        this.list = list;
    }

    public static class ApprovalModel {
        public long Approval_id;
        public LibraryModel library;
        public long Branch_id;
        public TransactionModel TransactionInfo;
        public RowStatusModel RowStatus;
        public String Library_Status;
        public String Library_Status_text;

        public ApprovalModel(String library_Status_text) {
            Library_Status_text = library_Status_text;
        }

        public ApprovalModel(long approval_id, LibraryModel library, long branch_id, TransactionModel transactionInfo, RowStatusModel rowStatus, String library_Status) {
            Approval_id = approval_id;
            this.library = library;
            Branch_id = branch_id;
            TransactionInfo = transactionInfo;
            RowStatus = rowStatus;
            Library_Status = library_Status;
        }

        public long getApproval_id() {
            return Approval_id;
        }

        public void setApproval_id(long approval_id) {
            Approval_id = approval_id;
        }

        public LibraryModel getLibrary() {
            return library;
        }

        public void setLibrary(LibraryModel library) {
            this.library = library;
        }

        public long getBranch_id() {
            return Branch_id;
        }

        public void setBranch_id(long branch_id) {
            Branch_id = branch_id;
        }

        public TransactionModel getTransactionInfo() {
            return TransactionInfo;
        }

        public void setTransactionInfo(TransactionModel transactionInfo) {
            TransactionInfo = transactionInfo;
        }

        public RowStatusModel getRowStatus() {
            return RowStatus;
        }

        public void setRowStatus(RowStatusModel rowStatus) {
            RowStatus = rowStatus;
        }

        public String getLibrary_Status() {
            return Library_Status;
        }

        public void setLibrary_Status(String library_Status) {
            Library_Status = library_Status;
        }

        public String getLibrary_Status_text() {
            return Library_Status_text;
        }

        public void setLibrary_Status_text(String library_Status_text) {
            Library_Status_text = library_Status_text;
        }
    }

    public static class LibraryStandardModel {
        public long library_std_id;
        public long std_id;
        public long sub_id;
        public String standard;
        public String subject;
        public long library_id;

        public long getLibrary_std_id() {
            return library_std_id;
        }

        public void setLibrary_std_id(long library_std_id) {
            this.library_std_id = library_std_id;
        }

        public long getStd_id() {
            return std_id;
        }

        public void setStd_id(long std_id) {
            this.std_id = std_id;
        }

        public long getSub_id() {
            return sub_id;
        }

        public void setSub_id(long sub_id) {
            this.sub_id = sub_id;
        }

        public String getStandard() {
            return standard;
        }

        public void setStandard(String standard) {
            this.standard = standard;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public long getLibrary_id() {
            return library_id;
        }

        public void setLibrary_id(long library_id) {
            this.library_id = library_id;
        }

    }

    public static class ApprovalData {
        ApprovalModel Data;
        boolean Completed;

        public ApprovalModel getData() {
            return Data;
        }

        public void setData(ApprovalModel data) {
            Data = data;
        }

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }
    }
}
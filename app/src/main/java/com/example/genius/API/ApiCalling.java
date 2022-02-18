package com.example.genius.API;

import com.example.genius.Model.AnnouncementModel;
import com.example.genius.Model.AnnouncementSingleModel;
import com.example.genius.Model.AnswerSheetData;
import com.example.genius.Model.AttendanceData;
import com.example.genius.Model.AttendanceModel;
import com.example.genius.Model.BannerData;
import com.example.genius.Model.BannerModel;
import com.example.genius.Model.BatchModel;
import com.example.genius.Model.BranchClassModel;
import com.example.genius.Model.BranchClassSingleModel;
import com.example.genius.Model.BranchCourseModel;
import com.example.genius.Model.BranchCourseSingleModel;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.BranchSubjectModel;
import com.example.genius.Model.BranchSubjectSingleModel;
import com.example.genius.Model.CategoryData;
import com.example.genius.Model.CircularModel;
import com.example.genius.Model.ClassModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.CourceModel;
import com.example.genius.Model.FacultyModel;
import com.example.genius.Model.FeeStructureData;
import com.example.genius.Model.FeeStructureSingleData;
import com.example.genius.Model.GalleryData;
import com.example.genius.Model.GalleryModel;
import com.example.genius.Model.HomeworkData;
import com.example.genius.Model.HomeworkModel;
import com.example.genius.Model.LibraryData;
import com.example.genius.Model.LibraryModel;
import com.example.genius.Model.LibrarySingleData;
import com.example.genius.Model.LinkData;
import com.example.genius.Model.LinkModel;
import com.example.genius.Model.MarksModel;
import com.example.genius.Model.NotificationData;
import com.example.genius.Model.NotificationModel;
import com.example.genius.Model.PaperByIdData;
import com.example.genius.Model.PaperData;
import com.example.genius.Model.PaperModel;
import com.example.genius.Model.ProfileModel;
import com.example.genius.Model.ReminderData;
import com.example.genius.Model.ReminderModel;
import com.example.genius.Model.SchoolData;
import com.example.genius.Model.SchoolModel;
import com.example.genius.Model.StaffData;
import com.example.genius.Model.StaffModel;
import com.example.genius.Model.StandardData;
import com.example.genius.Model.StandardModel;
import com.example.genius.Model.StudentData;
import com.example.genius.Model.StudentModel;
import com.example.genius.Model.SubjectData;
import com.example.genius.Model.SubjectModel;
import com.example.genius.Model.SuperAdminSubjectModel;
import com.example.genius.Model.TestScheduleData;
import com.example.genius.Model.TestScheduleModel;
import com.example.genius.Model.ToDoByIdData;
import com.example.genius.Model.TodoData;
import com.example.genius.Model.TodoModel;
import com.example.genius.Model.UploadPaperData;
import com.example.genius.Model.UploadPaperModel;
import com.example.genius.Model.UserData1;
import com.example.genius.Model.UserModel;
import com.example.genius.Model.UserRolesModel;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiCalling {

    @GET(ApiConstant.GET_ALL_BRANCH)
    Call<BranchModel> GetAllBranch();

    @GET(ApiConstant.VALIDATE_USER)
    Call<UserModel.UserData> ValidateUser(@Query("userName") String userName, @Query("password") String password);

    @GET(ApiConstant.GET_USER_PERMISSION)
    Call<UserModel.UserData> Get_User_Permission(@Query("BranchID") long BranchID);

    @GET(ApiConstant.GET_ALL_NOTIFICATION_BRANCH)
    Call<NotificationData> GetAllNotificationBranch(@Query("branchID") long branchID);

    @POST(ApiConstant.CHANGE_PASSWORD)
    Call<CommonModel> ChangePassword(@Query("userID") long userID, @Query("password") String password, @Query("oldPassword") String oldpassword);

    @POST(ApiConstant.FORGOT_PASSWORD)
    Call<CommonModel.ResponseModel> Forgot_Password(@Body UserModel userModel);

    @POST(ApiConstant.UPDATE_PROFILE)
    Call<ProfileModel> UpdateProfile(@Body StaffModel staffModel);

    @POST(ApiConstant.GET_STAFF_BY_ID)
    Call<StaffModel.StaffData1> GetStaffByID(@Query("StaffID") long StaffID);

    @POST(ApiConstant.GET_ALL_SCHOOL)
    Call<SchoolData> GetAllSchool(@Query("BranchID") long BranchID);

    @POST(ApiConstant.GET_ALL_STANDARD)
    Call<StandardData> GetAllStandard(@Query("BranchID") long BranchID);

    @POST(ApiConstant.GET_ALL_SUBJECT)
    Call<SubjectData> GetAllSubject(@Query("BranchID") long BranchID);

    @POST(ApiConstant.GET_ALL_STAFF)
    Call<StaffData> GetAllStaff(@Query("BranchID") long BranchID);

    @POST(ApiConstant.GET_ALL_STUDENT_WITHOUT_CONTENT)
    Call<StudentData> GetAllStudentWithoutContent(@Query("BranchID") long BranchID);

    @POST(ApiConstant.GET_ALL_ACTIVE_STUDENT_WITHOUT_CONTENT)
    Call<StudentData> GetAllActiveStudentWithoutContent(@Query("BranchID") long BranchID);

    @POST(ApiConstant.GET_ALL_INACTIVE_STUDENT_WITHOUT_CONTENT)
    Call<StudentData> GetAllInActiveStudentWithoutContent(@Query("BranchID") long BranchID);

    @POST(ApiConstant.GET_ALL_USER)
    Call<UserData1> GetAllUsers(@Query("branchID") long BranchID);

    @GET(ApiConstant.GET_ALL_USER_DDL)
    Call<UserData1> GetAllUsersddl(@Query("branchID") long BranchID);

    @GET(ApiConstant.GET_ALL_REMINDER_BY_BRANCH_USER)
    Call<ReminderData> GetAllReminderByBranchUser(@Query("branchID") long BranchID, @Query("userID") long userID);

    @GET(ApiConstant.GET_ALL_TODO_BY_BRANCH_WITHOUT_CONTENT)
    Call<TodoData> GetAllToDoWithoutContentByBranch(@Query("branchID") long BranchID);

    @GET(ApiConstant.GET_ALL_TODO_BY_BRANCH_WITHOUT_CONTENT_BRANCH)
    Call<TodoData> GetAllToDoWithoutContentByBranchAndUser(@Query("branchID") long BranchID, @Query("userID") long userID);

    @GET(ApiConstant.GET_TODO_BY_ID)
    Call<ToDoByIdData> GetToDoByHWID(@Query("todoID") long todoID);

    @POST(ApiConstant.GET_USER_ROLE_LIST)
    Call<UserRolesModel> GetUserRoleList();

    @POST(ApiConstant.USER_ROLE_MANAGEMENT)
    Call<CommonModel> UserRoleManagement(@Body UserModel userModel);

    @POST(ApiConstant.REMOVE_SUBJECT)
    Call<CommonModel> RemoveSubject(@Query("SubjectID") long SubjectID, @Query("lastupdatedby") String lastupdatedby);

    @POST(ApiConstant.REMOVE_SCHOOL)
    Call<CommonModel> RemoveSchool(@Query("SchoolID") long SchoolID, @Query("lastupdatedby") String lastupdatedby);

    @POST(ApiConstant.REMOVE_STAFF)
    Call<CommonModel> RemoveStaff(@Query("StaffID") long StaffID, @Query("lastupdatedby") String lastupdatedby);

    @POST(ApiConstant.REMOVE_STANDARD)
    Call<CommonModel> RemoveStandard(@Query("StandardID") long StandardID, @Query("lastupdatedby") String lastupdatedby);

    @POST(ApiConstant.REMOVE_NOTIFICATION)
    Call<CommonModel> RemoveNotification(@Query("notifID") long StandardID, @Query("lastupdatedby") String lastupdatedby);

    @POST(ApiConstant.STANDARD_MAINTENANCE)
    Call<StandardModel.StandardData1> StandardMaintenance(@Body StandardModel standardModel);

    @POST(ApiConstant.SCHOOL_MAINTENANCE)
    Call<SchoolModel.SchoolData1> SchoolMaintanance(@Body SchoolModel schoolModel);

    @POST(ApiConstant.SUBJECT_MAINTENANCE)
    Call<SubjectModel.SubjectData1> SubjectMaintanance(@Body SubjectModel subjectModel);

    @POST(ApiConstant.STAFF_MAINTENANCE)
    Call<StaffModel.StaffData1> StaffMaintanance(@Body StaffModel staffModel);

    @POST(ApiConstant.NOTIFICATION_MAINTENANCE)
    Call<NotificationModel.NotificationData1> NotificationMaintanance(@Body NotificationModel notificationModel);

    @GET(ApiConstant.GET_YOU_TUBE_LINKS_BRANCH)
    Call<LinkData> GetYoutubeLinksBranch(@Query("branchID") long branchID);

    @GET(ApiConstant.GET_LIVE_VIDEO_LINKS_BRANCH)
    Call<LinkData> GetLiveVideoLinksBranch(@Query("branchID") long branchID);

    @POST(ApiConstant.LIVE_VIDEO_MAINTENANCE)
    Call<LinkModel.LinkData1> LiveVideoMaintenance(@Body LinkModel linkModel);

    @POST(ApiConstant.YOUTUBE_MAINTENANCE)
    Call<LinkModel.LinkData1> YoutubeVideoMaintenance(@Body LinkModel linkModel);

    @POST(ApiConstant.REMOVE_LINK)
    Call<CommonModel> RemoveLink(@Query("uniqueID") long uniqueID, @Query("lastupdatedby") String lastupdatedby);

    @GET(ApiConstant.GET_ALL_IMAGES_BRANCH)
    Call<GalleryData> GetAllGalleryImagesBranch(@Query("branchID") long branchID);

    @POST(ApiConstant.REMOVE_IMAGE)
    Call<CommonModel> RemoveGallery(@Query("uniqueID") long uniqueID, @Query("lastupdatedby") String lastupdatedby);

    @POST(ApiConstant.REMINDER_MAINTENANCE)
    Call<ReminderModel.ReminderData1> ReminderMaintenance(@Body ReminderModel reminderModel);

    @GET(ApiConstant.GET_ALL_VIDEO_BRANCH)
    Call<GalleryData> GetAllGalleryVideoBranch(@Query("branchID") long branchID);

    @GET(ApiConstant.GET_ALL_BANNER_BRANCH)
    Call<BannerData> GetAllBannerBranch(@Query("branchID") long branchID);

    @GET(ApiConstant.GET_ALL_BANNER_BRANCH_TYPE)
    Call<BannerData> GetAllBannerBranchType(@Query("branchID") long branchID, @Query("bannerTypeID") int typeID);

    @POST(ApiConstant.REMOVE_BANNER)
    Call<CommonModel> RemoveBanner(@Query("bannerID") long bannerID, @Query("lastupdatedby") String lastupdatedby);

    @GET(ApiConstant.GET_ALL_MOBILE_LIBRARY)
    Call<LibraryData> GetAllMobileLibrary(@Query("Type") int Type, @Query("branchID") Long branchID);

    @POST(ApiConstant.REMOVE_LIBRARY)
    Call<CommonModel> RemoveLibrary(@Query("libraryID") long libraryID, @Query("lastupdatedby") String lastupdatedby);

    @GET(ApiConstant.GET_STUDENT_ATTENDANCE_LIST)
    Call<StudentData> GetAllStudentForAttendance(@Query("branchID") long branchID, @Query("stdID") long stdID, @Query("courseid") long courseid,@Query("batchID") int batchID, @Query("attendanceDate") String attendanceDate);

    @POST(ApiConstant.ATTENDANCE_MAINTENANCE)
    Call<AttendanceModel.AttendanceData1> AttendanceMaintenance(@Body AttendanceModel attendanceModel);

    @GET(ApiConstant.GET_ATTENDANCE_LIST)
    Call<AttendanceData> GetAllAttendanceByBranch(@Query("branchID") long branchID);

    @POST(ApiConstant.REMOVE_ATTENDANCE)
    Call<CommonModel> RemoveAttendance(@Query("attendanceID") long attendanceID, @Query("lastupdatedby") String lastupdatedby);

    @POST(ApiConstant.TESTSCHEDULE_MAINTENANCE)
    Call<TestScheduleModel.TestScheduleData1> TestMaintenance(@Body TestScheduleModel testScheduleModel);

    @GET(ApiConstant.GET_TESTSCHEDULE_BY_BRANCH_API)
    Call<TestScheduleData> GetAllTestByBranchAPI(@Query("branchID") long branchID);

    @POST(ApiConstant.UPLOAD_PAPER_MAINTENANCE)
    Call<UploadPaperModel.UploadPaperData1> TestPaperMaintenance(@Body UploadPaperModel uploadPaperModel);

    @GET(ApiConstant.GET_EDIT_PAPER_DATA)
    Call<UploadPaperData> GetAllTestPapaerByTest(@Query("testID") long testID);

    @GET(ApiConstant.GET_ATTENDANCE_BY_ID)
    Call<AttendanceModel.AttendanceData1> GetAttendanceByID(@Query("attendanceID") long attendanceID);

    @GET(ApiConstant.GET_ALL_PRACTICE_PAPER_WITHOUT_CONTENT)
    Call<PaperData> GetAllPaperWithoutContent(@Query("branchID") long branchID);

    @GET(ApiConstant.GET_PRACTICE_PAPER_BY_PAPERID)
    Call<PaperByIdData> GetPaperByPaperID(@Query("paperID") long paperID);

    @POST(ApiConstant.REMOVE_PAPER)
    Call<CommonModel> RemovePaper(@Query("paperID") long paperID, @Query("lastupdatedby") String lastupdatedby);

    @GET(ApiConstant.GET_ALL_HOMEWORK_WITHOUT_CONTENT)
    Call<HomeworkData> GetAllHomeworkWithoutContentByBranch(@Query("branchID") long branchID);

    @POST(ApiConstant.REMOVE_HOMEWORK)
    Call<CommonModel> RemoveHomework(@Query("hwID") long hwID, @Query("lastupdatedby") String lastupdatedby);

    @GET(ApiConstant.GET_ANSWERSHEET)
    Call<AnswerSheetData> GetAllAnsSheetByTest(@Query("testID") long testID);

    @POST(ApiConstant.REMOVE_REMINDER)
    Call<CommonModel> RemoveReminder(@Query("reminderID") long reminderID, @Query("lastupdatedby") String lastupdatedby);

    @POST(ApiConstant.REMOVE_TODO)
    Call<CommonModel> RemoveTodo(@Query("todoID") long todoID, @Query("lastupdatedby") String lastupdatedby);

    @POST(ApiConstant.REMOVE_TEST_SCHEDULE_PAPER)
    Call<CommonModel> RemoveTest(@Query("testID") long testID, @Query("lastupdatedby") String lastupdatedby, @Query("paper") Boolean paper);

    @Multipart
    @POST(ApiConstant.FEES_MAINTENANCE + "/{FeesID}"
            + "/{FeesDetailsID}" + "/{CourseID}" +"/{StandardID}" + "/{branchID}" + "/{Remark}" + "/{SubmitDate}"
            + "/{CreateId}" + "/{CreateBy}" + "/{TransactionId}" + "/{FileName}" + "/{Extension}" + "/{HasFile}")
    Call<FeeStructureSingleData> FeesMaintenance(@Path("FeesID") long FeesID,
                                                 @Path("FeesDetailsID") long FeesDetailsID, @Path("CourseID") long CourseID,@Path("StandardID") long StandardID,
                                                 @Path("branchID") long branchID, @Path("Remark") String Remark, @Path("SubmitDate") String SubmitDate,
                                                 @Path("CreateId") long CreateId, @Path("CreateBy") String CreateBy,
                                                 @Path("TransactionId") long TransactionId, @Path("FileName") String FileName,
                                                 @Path("Extension") String Extension, @Path("HasFile") Boolean HasFile, @Part MultipartBody.Part image);

    @GET(ApiConstant.GET_ALL_FEES_BRANCH)
    Call<FeeStructureData> GetFeesByBranchID(@Query("BranchID") long BranchID);

    @POST(ApiConstant.REMOVE_FEES)
    Call<CommonModel> RemoveFees(@Query("FeesID") long FeesID, @Query("lastupdatedby") String lastupdatedby);

    @GET(ApiConstant.GET_ALL_CATEGORY_BRANCH)
    Call<CategoryData> GetAllCategory(@Query("BranchID") long BranchID);

    @Multipart
    @POST(ApiConstant.LibraryMaintenance + "/{LibraryID}"
            + "/{LibraryDetailID}" + "/{Title}" + "/{link}" + "/{FileName}" + "/{Extension}" + "/{Description}" + "/{BranchID}" + "/{CategoryID}"
            + "/{CreateId}" + "/{CreateBy}" + "/{TransactionId}" + "/{HasFile}" + "/{Type}")
    Call<LibrarySingleData> LibraryMaintenance(@Path("LibraryID") long LibraryID,
                                               @Path("LibraryDetailID") long LibraryDetailID, @Path("Title") String Title,
                                               @Path("link") String link, @Path("FileName") String FileName, @Path("Extension") String Extension,
                                               @Path("Description") String Description, @Path("BranchID") long BranchID,
                                               @Path("CategoryID") long CategoryID, @Path("CreateId") int CreateId,
                                               @Path("CreateBy") String CreateBy, @Path("TransactionId") long TransactionId,
                                               @Path("HasFile") Boolean HasFile, @Path("Type") int Type, @Part MultipartBody.Part image);

    @Multipart
    @POST(ApiConstant.OldLibraryMaintenance + "/{LibraryID}"
            + "/{LibraryDetailID}" + "/{LibraryTitle}" + "/{CategoryID}" + "/{CourseID}" + "/{StandardID}" + "/{BranchID}" + "/{CreatebyBranch}" + "/{Type}" + "/{Library_Type}" + "/{Description}"
            + "/{SubjectID}" + "/{CreateId}" + "/{CreateBy}" + "/{TransactionId}" + "/{VideoLink}" + "/{ThumbnailFileName}" + "/{ThumbnailFileExtension}" + "/{DocFileName}"
            + "/{DocFileExtension}" + "/{HasThumbnailFile}" + "/{HasDocFile}")
    Call<LibrarySingleData> OldLibraryMaintenance(@Path("LibraryID") long LibraryID,
                                                  @Path("LibraryDetailID") long LibraryDetailID, @Path("LibraryTitle") String LibraryTitle,
                                                  @Path("CategoryID") long CategoryID, @Path("CourseID") long CourseID,@Path("StandardID") String StandardID, @Path("BranchID") long BranchID,
                                                  @Path("CreatebyBranch") long CreatebyBranch, @Path("Type") int Type, @Path("Library_Type") int Library_Type,
                                                  @Path(value = "Description") String Description, @Path("SubjectID") long SubjectID, @Path("CreateId") int CreateId,
                                                  @Path("CreateBy") String CreateBy, @Path("TransactionId") long TransactionId, @Path("VideoLink") String VideoLink,
                                                  @Path("ThumbnailFileName") String ThumbnailFileName, @Path("ThumbnailFileExtension") String ThumbnailFileExtension,
                                                  @Path("DocFileName") String DocFileName, @Path("DocFileExtension") String DocFileExtension,
                                                  @Path("HasThumbnailFile") Boolean HasThumbnailFile, @Path("HasDocFile") Boolean HasDocFile,
                                                  @Part MultipartBody.Part Thumbnail, @Part MultipartBody.Part Document);

    @GET(ApiConstant.GET_ALL_LIBRARY_BRANCH)
    Call<LibraryData> GetAllLibrary(@Query("Type") int Type, @Query("branchID") long branchID);

    @GET(ApiConstant.GET_LIBRARY_APPROVAL_LIST)
    Call<LibraryData> Getalllibraryapprovallist(@Query("branchID") long branchID);

    @GET(ApiConstant.GET_LIBRARY_APPROVAL_LIST_BRANCH)
    Call<LibraryData> GetLibraryApprovalByBranch(@Query("branchID") long branchID);

    @POST(ApiConstant.LIBRARY_APPROVAL_MAINTENANCE)
    Call<LibraryModel.ApprovalData> Library_Approval_Maintenanace(@Body LibraryModel.ApprovalModel approvalModel);

    @POST(ApiConstant.REMOVE_NEW_LIBRARY)
    Call<CommonModel> RemoveNewLibrary(@Query("libraryID") long FeesID, @Query("lastupdatedby") String lastupdatedby);

    @POST(ApiConstant.LibraryLinkMaintenance)
    Call<LibrarySingleData> LibraryLinkMaintenance(@Body LibraryModel libraryModel);

    @Multipart
    @POST(ApiConstant.NEW_HOMEWORK_MAINTENANCE + "/{HomeworkID}"
            + "/{Homework_Date}" + "/{BranchID}" + "/{CourseID}" + "/{StandardID}" + "/{SubjectID}" + "/{Batch_TimeID}" + "/{Remark}" + "/{CreateId}" + "/{CreateBy}"
            + "/{TransactionId}" + "/{FileName}" + "/{Extension}" + "/{HasFile}")
    Call<HomeworkModel.HomeworkData1> HomeworkMaintenance(@Path("HomeworkID") long HomeworkID,
                                                          @Path("Homework_Date") String Homework_Date, @Path("BranchID") long BranchID,@Path("CourseID") long CourseID,
                                                          @Path("StandardID") long StandardID, @Path("SubjectID") long SubjectID, @Path("Batch_TimeID") int Batch_TimeID,
                                                          @Path("Remark") String Remark, @Path("CreateId") long CreateId,
                                                          @Path("CreateBy") String CreateBy, @Path("TransactionId") long TransactionId,
                                                          @Path("FileName") String FileName, @Path("Extension") String Extension,
                                                          @Path("HasFile") Boolean HasFile, @Part MultipartBody.Part image);

    @Multipart
    @POST(ApiConstant.NEW_UPLOAD_PAPER_MAINTENANCE + "/{TestID}"
            + "/{TestPaperID}" + "/{Paper_Type}" + "/{Doc_Link}" + "/{Paper_Remark}" + "/{CreateId}" + "/{CreateBy}"
            + "/{TransactionId}" + "/{FileName}" + "/{Extension}" + "/{HasFile}")
    Call<UploadPaperModel.UploadPaperData1> TestPaperMaintenance(@Path("TestID") long TestID,
                                                                 @Path("TestPaperID") long TestPaperID, @Path("Paper_Type") int Paper_Type,
                                                                 @Path("Doc_Link") String Doc_Link, @Path("Paper_Remark") String Paper_Remark,
                                                                 @Path("CreateId") long CreateId, @Path("CreateBy") String CreateBy,
                                                                 @Path("TransactionId") long TransactionId,
                                                                 @Path("FileName") String FileName, @Path("Extension") String Extension,
                                                                 @Path("HasFile") Boolean HasFile, @Part MultipartBody.Part image);

    @Multipart
    @POST(ApiConstant.GALLRY_IMAGE_MAINTENANCE + "/{UniqID}/{BranchID}/{Remark}/{UploadType}/{CreateId}/{CreateBy}/{TransactionId}/{FileName}/{Extension}/{HasFile}")
    Call<GalleryModel.GallaryData1> GalleryImageMaintenance(@Path("UniqID") long UniqID, @Path("BranchID") long BranchID, @Path("Remark") String Remark, @Path("UploadType") int UploadType,
                                                            @Path("CreateId") long CreateId, @Path("CreateBy") String CreateBy, @Path("TransactionId") long TransactionId, @Path("FileName") String FileName,
                                                            @Path("Extension") String Extension, @Path("HasFile") Boolean HasFile, @Part MultipartBody.Part image);

    @Multipart
    @POST(ApiConstant.PRACTICE_PAPER_MAINTENANCE + "/{PaperID}/{UniqID}/{BranchID}/{CourseID}/{StandardID}/{SubjectID}/{Batch_TimeID}/{Remark}/{CreateId}/{CreateBy}/{TransactionId}/{FileName}/{Extension}/{HasFile}")
    Call<PaperModel.PaperData1> PaperMaintenance(@Path("PaperID") long PaperID, @Path("UniqID") long UniqID, @Path("BranchID") long BranchID, @Path("CourseID") long CourseID,@Path("StandardID") long StandardID, @Path("SubjectID") long SubjectID, @Path("Batch_TimeID") int Batch_TimeID, @Path("Remark") String Remark,
                                                 @Path("CreateId") long CreateId, @Path("CreateBy") String CreateBy, @Path("TransactionId") long TransactionId, @Path("FileName") String FileName,
                                                 @Path("Extension") String Extension, @Path("HasFile") Boolean HasFile, @Part MultipartBody.Part image);

    @Multipart
    @POST(ApiConstant.BANNER_MAINTENANCE + "/{BannerID}/{BranchID}/{isAdmin}/{isTeacher}/{isStudent}/{CreateId}/{CreateBy}/{TransactionId}/{FileName}/{Extension}/{HasFile}")
    Call<BannerModel.BannerlData1> BannerMaintenance(@Path("BannerID") long BannerID, @Path("BranchID") long BranchID, @Path("isAdmin") Boolean isAdmin, @Path("isTeacher") Boolean isTeacher, @Path("isStudent") Boolean isStudent,
                                                     @Path("CreateId") long CreateId, @Path("CreateBy") String CreateBy, @Path("TransactionId") long TransactionId, @Path("FileName") String FileName,
                                                     @Path("Extension") String Extension, @Path("HasFile") Boolean HasFile, @Part MultipartBody.Part image);

    @Multipart
    @POST(ApiConstant.TODO_MAINTENANCE + "/{ToDoID}/{ToDo_Date}/{BranchID}/{UserID}/{ToDo_Description}/{CreateId}/{CreateBy}/{TransactionId}/{FileName}/{Extension}/{HasFile}")
    Call<TodoModel.TodoData1> ToDoMaintenance(@Path("ToDoID") long ToDoID, @Path("ToDo_Date") String ToDo_Date, @Path("BranchID") long BranchID, @Path("UserID") long UserID, @Path("ToDo_Description") String ToDo_Description,
                                              @Path("CreateId") long CreateId, @Path("CreateBy") String CreateBy, @Path("TransactionId") long TransactionId, @Path("FileName") String FileName,
                                              @Path("Extension") String Extension, @Path("HasFile") Boolean HasFile, @Part MultipartBody.Part image);

    @POST(ApiConstant.GET_TEST_MARKS_DATE)
    Call<MarksModel.MarksData> Get_Test_Marks(@Query("BranchID") long BranchID, @Query("courseID") long courseID,@Query("stdID") long stdID, @Query("BatchType") int BatchType);

    @Multipart
    @POST(ApiConstant.STUDENT_MAINTENANCE + "/{StudentID}/{ParentID}/{Gr_No}/{Name}/{Birth_Date}/{Address}/{BranchID}/{StandardID}/{SchoolID}/{School_TimeID}/{Batch_TimeID}/{Last_Year_Result}/{Grade}/{Class_Name}/{Student_Contact_No}/{Admission_Date}/{Parent_Name}/{Father_Occupation}/{Mother_Occupation}/{Parent_Contact_No}/{CreateId}/{CreateBy}/{TransactionId}/{std_pwd}/{parent_pwd}/{FileName}/{Extension}/{HasFile}")
    Call<StudentModel.StudentData1> StudentMaintenance(@Path("StudentID") long StudentID, @Path("ParentID") long ParentID, @Path("Gr_No") String Gr_No, @Path("Name") String Name, @Path("Birth_Date") String Birth_Date, @Path("Address") String Address, @Path("BranchID") long BranchID, @Path("StandardID") String StandardID, @Path("SchoolID") long SchoolID,
                                                       @Path("School_TimeID") int School_TimeID, @Path("Batch_TimeID") int Batch_TimeID, @Path("Last_Year_Result") String Last_Year_Result, @Path("Grade") String Grade, @Path("Class_Name") String Class_Name,
                                                       @Path("Student_Contact_No") String Student_Contact_No, @Path("Admission_Date") String Admission_Date, @Path("Parent_Name") String Parent_Name, @Path("Father_Occupation") String Father_Occupation, @Path("Mother_Occupation") String Mother_Occupation,
                                                       @Path("Parent_Contact_No") String Parent_Contact_No, @Path("CreateId") long CreateId, @Path("CreateBy") String CreateBy, @Path("TransactionId") long TransactionId, @Path("std_pwd") String std_pwd, @Path("parent_pwd") String parent_pwd, @Path("FileName") String FileName,
                                                       @Path("Extension") String Extension, @Path("HasFile") Boolean HasFile, @Part MultipartBody.Part image);

    @GET(ApiConstant.UPDATE_HOMEWORK_CHECKING)
    Call<HomeworkModel.HomeworkDetailData> Update_Homework_Checking(@Query("HomeworkID") long HomeworkID, @Query("StudentID") long StudentID, @Query("Remark") String Remark, @Query("Status") int Status, @Query("CreatedBy") String CreatedBy, @Query("CreatedId") long CreatedId);

    @GET(ApiConstant.GET_HOMEWORK_CHECKING_LIST)
    Call<HomeworkModel.HomeworkDetailData> Get_Homework_Checking_List(@Query("hwID") long hwID);

    @GET(ApiConstant.UPDATE_TEST_PAPER_CHECKING)
    Call<AnswerSheetData> Update_Test_Paper_Checking(@Query("TestID") long TestID, @Query("StudentID") long StudentID, @Query("Remark") String Remark, @Query("Status") int Status, @Query("CreatedBy") String CreatedBy, @Query("CreatedId") long CreatedId);

    @POST(ApiConstant.GET_TEST_DETAILS)
    Call<TestScheduleModel.TestScheduleData1> Get_Test_Details(@Query("TestID") long TestID, @Query("SubjectID") long SubjectID);

    @POST(ApiConstant.GET_STUDENT_DEATILS)
    Call<StudentModel.StudentDataList> Get_Student_Details(@Query("Std") long Std, @Query("Branch") long Branch, @Query("BatchTime") long BatchTime);

    @Multipart
    @POST(ApiConstant.MARKS_MAITENANCE + "/{MarksID}/{Marks_Date}/{TestID}/{BranchID}/{StudentID}/{Achieve_Marks}/{BatchtimeID}/{CourseID}/{StandardID}/{SubjectID}/{CreateId}/{CreateBy}/{TransactionId}/{FileName}/{Extension}/{HasFile}")
    Call<MarksModel.MarksData1> MarksMaintenance(@Path("MarksID") long MarksID, @Path("Marks_Date") String Marks_Date, @Path("TestID") long TestID,
                                                 @Path("BranchID") long BranchID, @Path("StudentID") String StudentID, @Path("Achieve_Marks") String Achieve_Marks,
                                                 @Path("BatchtimeID") int BatchtimeID, @Path("CourseID") long CourseID,@Path("StandardID") long StandardID,@Path("SubjectID") long SubjectID,
                                                 @Path("CreateId") long CreateId, @Path("CreateBy") String CreateBy, @Path("TransactionId") long TransactionId, @Path("FileName") String FileName, @Path("Extension") String Extension,
                                                 @Path("HasFile") Boolean HasFile, @Part MultipartBody.Part image);

    @GET(ApiConstant.GET_ALL_SUBJECT_BY_TESTDATE)
    Call<SubjectData> GetAllSubjectByTestDate(@Query("TestDate") String TestDate);

    @POST(ApiConstant.GET_ALL_STUDENT_ACHIEVE_MARK)
    Call<MarksModel.MarksData> GetAllStudentAchieveMarks(@Query("Std") long Std, @Query("Branch") long Branch, @Query("Batch") long Batch, @Query("MarksID") long MarksID);

    @POST(ApiConstant.UPDATE_ACHIEVE_MARKS)
    Call<MarksModel.MarksData> Update_Achieve_Marks(@Query("MarksID") long MarksID, @Query("StudentID") long StudentID, @Query("AchieveMarks") String AchieveMarks, @Query("CreatedId") long CreatedId, @Query("CreatedBy") String CreatedBy, @Query("TransactionId") long TransactionId);

    //Branch Course
    @POST(ApiConstant.BRANCH_COURCE_MAITENANCE)
    Call<BranchCourseSingleModel> BranchCourseMaintenance(@Body BranchCourseModel.BranchCourceData libraryModel);

    @POST(ApiConstant.GET_BRANCH_COURCE_BRANCHCOURCE_BY_ID)
    Call<BranchCourseModel> GetBranchCourseByBranchCourseID(@Query("BranchCourseID") long BranchCourseID);

    @POST(ApiConstant.GET_ALL_BRANCH_COURSE)
    Call<BranchCourseModel> Get_All_Branch_Course(@Query("branchID") long branchID);

    @GET(ApiConstant.GET_BRANCH_COURCE_ALL)
    Call<CourceModel> GetAllCourse();

    @GET(ApiConstant.GET_ALL_COURSE_DDL)
    Call<BranchCourseModel> GetAllCourseDDL(@Query("BranchID") long BranchID);

    @GET(ApiConstant.GET_BRANCH_COURSE_SPINNER)
    Call<BranchCourseModel> Get_Course_Spinner(@Query("BranchID") long BranchID);

    @GET(ApiConstant.GET_BRANCH_CLASS_SPINNER)
    Call<BranchClassModel> Get_Class_Spinner(@Query("BranchID") long BranchID, @Query("CourseID") long CourseID);

    @GET(ApiConstant.GET_ALL_CLASS_BY_COURSE)
    Call<BranchClassModel> Get_All_Class_By_Course(@Query("CourseID") long CourseID);

    @GET(ApiConstant.CHECK_CLASS_DETAIL)
    Call<CommonModel.ResponseModel> Check_Class_Detail(@Query("ClassID") long ClassID, @Query("BranchID") long BranchID);

    @GET(ApiConstant.CHECK_SUBJECT_DETAIL)
    Call<CommonModel.ResponseModel> Check_Subject_Detail(@Query("Branchid") long Branchid, @Query("subjectdetailis") long subjectdetailis);

    @POST(ApiConstant.BRANCH_COURCE_REMOVE)
    Call<CommonModel> RemoveBranchCourse(@Query("BranchCourseID") long BranchCourseID, @Query("lastupdatedby") String lastupdatedby);

    //Branch Class
    @POST(ApiConstant.BRANCH_CLASS_MAITENANCE)
    Call<BranchClassSingleModel> BranchClassMaintenance(@Body BranchClassSingleModel.BranchClassData libraryModel);

    @GET(ApiConstant.GET_BRANCH_CLASS_ALL)
    Call<ClassModel> GetAllClass();

    @POST(ApiConstant.GET_BRANCH_CLASS_BRANCHID)
    Call<BranchClassModel> GetAllBranchClassByBranchID(@Query("BranchID") long BranchID);

    @GET(ApiConstant.BRANCH_CLASS_REMOVE)
    Call<CommonModel.ResponseModel> RemoveClassDetail(@Query("ClassID") long ClassID, @Query("BranchID") long BranchID, @Query("lastupdatedby") long lastupdatedby);

    //Branch Subject
    @POST(ApiConstant.BRANCH_SUBJECT_MAITENANCE)
    Call<BranchSubjectSingleModel> BranchSubjectMaintenance(@Body BranchSubjectModel.BranchSubjectData libraryModel);

    @POST(ApiConstant.GET_BRANCH_SUBJECT_BRANCHID)
    Call<BranchSubjectModel> GetAllBranchSubjectByBranchID(@Query("branchID") long branchID);

    @GET(ApiConstant.GET_BRANCH_SUBJECT_ALL)
    Call<SuperAdminSubjectModel> GetAllSubject();

    @POST(ApiConstant.BRANCH_SUBJECT_REMOVE)
    Call<CommonModel.ResponseModel> RemoveSubjectDetail(@Query("CourseID") long CourseID, @Query("ClassID") long ClassID, @Query("BranchID") long BranchID, @Query("lastupdatedby") long lastupdatedby);

    @POST(ApiConstant.GET_ALL_BRANCH_SUBJECT)
    Call<BranchSubjectModel> Get_All_Subject(@Query("SubjectID") long SubjectID, @Query("BranchID") long BranchID, @Query("ClassID") long ClassID);

    @GET(ApiConstant.GET_ALL_SUBJECT_DDL)
    Call<BranchSubjectModel> Get_All_Subject_DDL(@Query("ClassID") long ClassID, @Query("BranchID") long BranchID, @Query("CourseID") long CourseID);

    @GET(ApiConstant.GET_LIBRARY_SUBJECT_DDL)
    Call<BranchSubjectModel> Get_Library_Subject_DDL(@Query("CourseID") long CourseID, @Query("BranchID") long BranchID, @Query("ClassID") String ClassID);

    @GET(ApiConstant.GET_ALL_SUBJECT_BY_COURSE)
    Call<BranchSubjectModel> Get_All_Subject_By_Course(@Query("courseid") long courseid, @Query("classid") long classid);

    @GET(ApiConstant.DOWNLOAD_STUDENT_HOMEWORK + "/{HomeworkID}/{StudentID}/{Homework}/{Student}/{Class}")
    Call<HomeworkModel.HomeworkData1> Download_Student_Homework(@Path("HomeworkID") long HomeworkID, @Path("StudentID") long StudentID, @Path("Homework") String Homework,
                                                                @Path("Student") String Student, @Path("Class") String Class);

    @GET(ApiConstant.DOWNLOAD_STUDENT_TEST_PAPER + "/{TestID}/{StudentID}/{Homework}/{Student}/{Class}")
    Call<HomeworkModel.HomeworkData1> Download_Student_Test_Paper(@Path("TestID") long TestID, @Path("StudentID") long StudentID, @Path("Homework") String Homework,
                                                                  @Path("Student") String Student, @Path("Class") String Class);

    @POST(ApiConstant.FACULTY_REMOVE)
    Call<CommonModel> Remove_Faculty(@Query("facultyID") long facultyID, @Query("lastupdatedby") String lastupdatedby);

    @GET(ApiConstant.GET_FACULTY_LIST)
    Call<FacultyModel.FacultyData> Get_All_Faculty(@Query("BranchID") long BranchID);

    @Multipart
    @POST(ApiConstant.FACULTY_MAINTENANCE + "/{facultyID}/{StaffID}/{Subject_dtl_id}/{course_dtl_id}/{Class_dtl_id}/{BranchID}/{Descripation}/{CreateId}/{CreateBy}/{TransactionId}/{FileName}/{Extension}/{HasFile}")
    Call<FacultyModel.FacultyModelData> Faculty_Maintanance(@Path("facultyID") long facultyID, @Path("StaffID") long StaffID,
                                                            @Path("Subject_dtl_id") long Subject_dtl_id, @Path("course_dtl_id") long course_dtl_id,
                                                            @Path("Class_dtl_id") long Class_dtl_id, @Path("BranchID") long BranchID, @Path("Descripation") String Descripation,
                                                            @Path("CreateId") long CreateId, @Path("CreateBy") String CreateBy, @Path("TransactionId") long TransactionId, @Path("FileName") String FileName, @Path("Extension") String Extension,
                                                            @Path("HasFile") Boolean HasFile, @Part MultipartBody.Part image);

    @POST(ApiConstant.AnnouncementMaintenance)
    Call<AnnouncementSingleModel> AnnouncementMaintenance(@Body AnnouncementModel.AnnouncementData announcementData);

    @GET(ApiConstant.GET_ALL_ANNOUNCEMENT)
    Call<AnnouncementModel> GetAllAnnouncement(@Query("branchID") long branchID);

    @POST(ApiConstant.REMOVE_ANNOUNCEMENT)
    Call<CommonModel> RemoveAnnouncement(@Query("annoID") long annoID, @Query("lastupdatedby") String lastupdatedby);

    @GET(ApiConstant.GET_CIRCULAR)
    Call<CircularModel> GetAllCircular();

    @POST(ApiConstant.GET_ALL_BATCH_LIST)
    Call<BatchModel.BatchData> Get_All_Batch_List(@Query("branchID") long branchID);

    @POST(ApiConstant.BATCH_MAINTAINANCE)
    Call<BatchModel.BatchResponseModel> Save_Batch(@Body BatchModel batchModel);

    @POST(ApiConstant.REMOVE_BATCH)
    Call<CommonModel> Remove_Batch(@Query("BatchID") long BatchID,@Query("lastupdatedby") String lastupdatedby);
}

package com.example.genius.API;

import com.example.genius.Model.AnswerSheetData;
import com.example.genius.Model.AttendanceData;
import com.example.genius.Model.AttendanceModel;
import com.example.genius.Model.BannerData;
import com.example.genius.Model.BannerModel;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.GalleryData;
import com.example.genius.Model.GalleryModel;
import com.example.genius.Model.HomeworkByIdData;
import com.example.genius.Model.HomeworkData;
import com.example.genius.Model.HomeworkModel;
import com.example.genius.Model.LibraryData;
import com.example.genius.Model.LibraryModel;
import com.example.genius.Model.LinkData;
import com.example.genius.Model.LinkModel;
import com.example.genius.Model.NotificationData;
import com.example.genius.Model.NotificationModel;
import com.example.genius.Model.PaperByIdData;
import com.example.genius.Model.PaperData;
import com.example.genius.Model.PaperModel;
import com.example.genius.Model.ReminderData;
import com.example.genius.Model.ReminderModel;
import com.example.genius.Model.RolesModel;
import com.example.genius.Model.SchoolData;
import com.example.genius.Model.SchoolModel;
import com.example.genius.Model.StaffData;
import com.example.genius.Model.StaffModel;
import com.example.genius.Model.StandardData;
import com.example.genius.Model.StandardModel;
import com.example.genius.Model.StudentByIdData;
import com.example.genius.Model.StudentData;
import com.example.genius.Model.StudentModel;
import com.example.genius.Model.SubjectData;
import com.example.genius.Model.SubjectModel;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiCalling {

    @GET(ApiConstant.GET_ALL_BRANCH)
    Call<BranchModel> GetAllBranch();

    @GET(ApiConstant.VALIDATE_USER)
    Call<UserModel.UserData> ValidateUser(@Query("userName") String userName, @Query("password") String password);

    @GET(ApiConstant.GET_ALL_NOTIFICATION)
    Call<NotificationData> GetAllNotification();

    @GET(ApiConstant.GET_ALL_NOTIFICATION_BRANCH)
    Call<NotificationData> GetAllNotificationBranch(@Query("branchID") long branchID);

    @GET(ApiConstant.GET_ALL_NOTIFICATION_BRANCH_TYPE)
    Call<NotificationData> GetAllNotificationBranchType(@Query("branchID") long branchID, @Query("typeID") int typeID);

    @POST(ApiConstant.CHANGE_PASSWORD)
    Call<CommonModel> ChangePassword(@Query("userID") long userID, @Query("password") String password, @Query("oldPassword") String oldpassword);

    @POST(ApiConstant.GET_ALL_SCHOOL)
    Call<SchoolData> GetAllSchool(@Query("BranchID") long BranchID);

    @POST(ApiConstant.GET_ALL_STANDARD)
    Call<StandardData> GetAllStandard(@Query("BranchID") long BranchID);

    @POST(ApiConstant.GET_ALL_SUBJECT)
    Call<SubjectData> GetAllSubject(@Query("BranchID") long BranchID);

    @POST(ApiConstant.GET_ALL_STAFF)
    Call<StaffData> GetAllStaff(@Query("BranchID") long BranchID);

    @POST(ApiConstant.GET_ALL_STUDENT)
    Call<StudentData> GetAllStudent(@Query("BranchID") long BranchID);

    @POST(ApiConstant.GET_STUDENT_BY_ID)
    Call<StudentByIdData> GetStudentByID(@Query("studenID") long BranchID);

    @POST(ApiConstant.GET_ALL_STUDENT_WITHOUT_CONTENT)
    Call<StudentData> GetAllStudentWithoutContent(@Query("BranchID") long BranchID);

    @POST(ApiConstant.REMOVE_STUDENT)
    Call<CommonModel> RemoveStudent(@Query("StudentID") long StudentID, @Query("lastupdatedby") String lastupdatedby);

    @POST(ApiConstant.GET_ALL_ACTIVE_STUDENT)
    Call<StudentData> GetAllActiveStudent(@Query("BranchID") long BranchID);

    @POST(ApiConstant.GET_ALL_INACTIVE_STUDENT)
    Call<StudentData> GetAllInactiveStudent(@Query("BranchID") long BranchID);

    @POST(ApiConstant.GET_ALL_ACTIVE_STUDENT_WITHOUT_CONTENT)
    Call<StudentData> GetAllActiveStudentWithoutContent(@Query("BranchID") long BranchID);

    @POST(ApiConstant.GET_ALL_INACTIVE_STUDENT_WITHOUT_CONTENT)
    Call<StudentData> GetAllInActiveStudentWithoutContent(@Query("BranchID") long BranchID);

    @POST(ApiConstant.GET_ALL_USER)
    Call<UserData1> GetAllUsers(@Query("branchID") long BranchID);

    @GET(ApiConstant.GET_ALL_REMINDER_BY_BRANCH)
    Call<ReminderData> GetAllReminderByBranch(@Query("branchID") long BranchID);

    @GET(ApiConstant.GET_ALL_REMINDER_BY_BRANCH_USER)
    Call<ReminderData> GetAllReminderByBranchUser(@Query("branchID") long BranchID, @Query("userID") long userID);

    @GET(ApiConstant.GET_ALL_TODO_BY_BRANCH)
    Call<TodoData> GetAllTodoByBranch(@Query("branchID") long BranchID);

    @GET(ApiConstant.GET_ALL_TODO_BY_BRANCH_WITHOUT_CONTENT)
    Call<TodoData> GetAllToDoWithoutContentByBranch(@Query("branchID") long BranchID);

    @GET(ApiConstant.GET_ALL_TODO_BY_BRANCH_USER)
    Call<TodoData> GetAllTodoByBranchUser(@Query("branchID") long BranchID, @Query("userID") long userID);

    @GET(ApiConstant.GET_ALL_TODO_BY_BRANCH_WITHOUT_CONTENT_BRANCH)
    Call<TodoData> GetAllToDoWithoutContentByBranchAndUser(@Query("branchID") long BranchID, @Query("userID") long userID);

    @GET(ApiConstant.GET_TODO_BY_ID)
    Call<ToDoByIdData> GetToDoByHWID(@Query("todoID") long todoID);

    @POST(ApiConstant.GET_USER_ROLE)
    Call<List<RolesModel>> GetUserRole(@Query("userID") long userID);

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

    @POST(ApiConstant.STUDENT_MAINTENANCE)
    Call<StudentModel.StudentData1> StudentMaintanance(@Body StudentModel studentModel);

    @POST(ApiConstant.NOTIFICATION_MAINTENANCE)
    Call<NotificationModel.NotificationData1> NotificationMaintanance(@Body NotificationModel notificationModel);

    @GET(ApiConstant.GET_YOU_TUBE_LINKS)
    Call<LinkData> GetYoutubeLinks();

    @GET(ApiConstant.GET_YOU_TUBE_LINKS_BRANCH)
    Call<LinkData> GetYoutubeLinksBranch(@Query("branchID") long branchID);

    @GET(ApiConstant.GET_LIVE_VIDEO_LINKS)
    Call<LinkData> GetLiveVideoLinks();

    @GET(ApiConstant.GET_LIVE_VIDEO_LINKS_BRANCH)
    Call<LinkData> GetLiveVideoLinksBranch(@Query("branchID") long branchID);

    @POST(ApiConstant.LIVE_VIDEO_MAINTENANCE)
    Call<LinkModel.LinkData1> LiveVideoMaintenance(@Body LinkModel linkModel);

    @POST(ApiConstant.YOUTUBE_MAINTENANCE)
    Call<LinkModel.LinkData1> YoutubeVideoMaintenance(@Body LinkModel linkModel);

    @POST(ApiConstant.REMOVE_LINK)
    Call<CommonModel> RemoveLink(@Query("uniqueID") long uniqueID, @Query("lastupdatedby") String lastupdatedby);

    @POST(ApiConstant.BANNER_MAINTENANCE)
    Call<BannerModel.BannerlData1> BannerMaintenance(@Body BannerModel bannerModel);

    @POST(ApiConstant.GALLRY_IMAGE_MAINTENANCE)
    Call<GalleryModel.GallaryData1> GalaryImageMaintenance(@Body GalleryModel galleryModel);

    @GET(ApiConstant.GET_ALL_IMAGES)
    Call<GalleryData> GetAllGalleryImages();

    @GET(ApiConstant.GET_ALL_IMAGES_BRANCH)
    Call<GalleryData> GetAllGalleryImagesBranch(@Query("branchID") long branchID);

    @POST(ApiConstant.REMOVE_IMAGE)
    Call<CommonModel> RemoveGallery(@Query("uniqueID") long uniqueID, @Query("lastupdatedby") String lastupdatedby);

    @POST(ApiConstant.GALLRY_VIDEO_MAINTENANCE)
    Call<GalleryModel.GallaryData1> GalaryVideoMaintenance(@Body GalleryModel galleryModel);

    @POST(ApiConstant.GET_REMINDER_BY_ID)
    Call<ReminderModel.ReminderData1> GetReminderById(@Query("reminderID") long reminderID);

    @POST(ApiConstant.REMINDER_MAINTENANCE)
    Call<ReminderModel.ReminderData1> ReminderMaintenance(@Body ReminderModel reminderModel);

    @POST(ApiConstant.TODO_MAINTENANCE)
    Call<TodoModel.TodoData1> TodoMaintenance(@Body TodoModel todoModel);

    @GET(ApiConstant.GET_ALL_VIDEO)
    Call<GalleryData> GetAllGalleryVideo();

    @GET(ApiConstant.GET_ALL_VIDEO_BRANCH)
    Call<GalleryData> GetAllGalleryVideoBranch(@Query("branchID") long branchID);

    @GET(ApiConstant.GET_ALL_BANNER_BRANCH)
    Call<BannerData> GetAllBannerBranch(@Query("branchID") long branchID);

    @GET(ApiConstant.GET_ALL_BANNER_BRANCH_TYPE)
    Call<BannerData> GetAllBannerBranchType(@Query("branchID") long branchID, @Query("bannerTypeID") int typeID);

    @GET(ApiConstant.GET_ALL_BANNER)
    Call<BannerData> GetAllBanner();

    @POST(ApiConstant.REMOVE_BANNER)
    Call<CommonModel> RemoveBanner(@Query("bannerID") long bannerID, @Query("lastupdatedby") String lastupdatedby);

    @POST(ApiConstant.PRACTICE_PAPER_MAINTENANCE)
    Call<PaperModel.PaperData1> PaperMaintenance(@Body PaperModel paperModel);

    @POST(ApiConstant.LIBRARY_MAINTENANCE)
    Call<LibraryModel.LibraryData1> LibraryMaintenance(@Body LibraryModel libraryModel);

    @GET(ApiConstant.GET_ALL_LIBRARY)
    Call<LibraryData> GetAllLibrary();

    @POST(ApiConstant.REMOVE_LIBRARY)
    Call<CommonModel> RemoveLibrary(@Query("libraryID") long libraryID, @Query("lastupdatedby") String lastupdatedby);

    @GET(ApiConstant.GET_STUDENT_ATTENDANCE_LIST)
    Call<StudentData> GetAllStudentForAttendance(@Query("branchID") long branchID, @Query("stdID") long stdID, @Query("batchID") int batchID);

    @POST(ApiConstant.ATTENDANCE_MAINTENANCE)
    Call<AttendanceModel.AttendanceData1> AttendanceMaintenance(@Body AttendanceModel attendanceModel);

    @GET(ApiConstant.GET_ATTENDANCE_LIST)
    Call<AttendanceData> GetAllAttendanceByBranch(@Query("branchID") long branchID);

    @POST(ApiConstant.REMOVE_ATTENDANCE)
    Call<CommonModel> RemoveAttendance(@Query("attendanceID") long attendanceID, @Query("lastupdatedby") String lastupdatedby);

    @POST(ApiConstant.TESTSCHEDULE_MAINTENANCE)
    Call<TestScheduleModel.TestScheduleData1> TestMaintenance(@Body TestScheduleModel testScheduleModel);

    @GET(ApiConstant.GET_TESTSCHEDULE_BY_BRANCH)
    Call<TestScheduleData> GetAllTestByBranch(@Query("branchID") long branchID);

    @POST(ApiConstant.UPLOAD_PAPER_MAINTENANCE)
    Call<UploadPaperModel.UploadPaperData1> TestPaperMaintenance(@Body UploadPaperModel uploadPaperModel);

    @GET(ApiConstant.GET_EDIT_PAPER_DATA)
    Call<UploadPaperData> GetAllTestPapaerByTest(@Query("testID") long testID);

    @GET(ApiConstant.GET_ATTENDANCE_BY_ID)
    Call<AttendanceModel.AttendanceData1> GetAttendanceByID(@Query("attendanceID") long attendanceID);

    @GET(ApiConstant.GET_ALL_PRACTICE_PAPER)
    Call<PaperData> GetAllPaper(@Query("branchID") long branchID);

    @GET(ApiConstant.GET_ALL_PRACTICE_PAPER_WITHOUT_CONTENT)
    Call<PaperData> GetAllPaperWithoutContent(@Query("branchID") long branchID);

    @GET(ApiConstant.GET_PRACTICE_PAPER_BY_PAPERID)
    Call<PaperByIdData> GetPaperByPaperID(@Query("paperID") long paperID);

    @POST(ApiConstant.REMOVE_PAPER)
    Call<CommonModel> RemovePaper(@Query("paperID") long paperID, @Query("lastupdatedby") String lastupdatedby);

    @POST(ApiConstant.HOMEWORK_MAINTENANCE)
    Call<HomeworkModel.HomeworkData1> HomeworkMaintenance(@Body HomeworkModel homeworkModel);

    @GET(ApiConstant.GET_ALL_HOMEWORK)
    Call<HomeworkData> GetAllHomeworkByBranch(@Query("branchID") long branchID);

    @GET(ApiConstant.GET_ALL_HOMEWORK_WITHOUT_CONTENT)
    Call<HomeworkData> GetAllHomeworkWithoutContentByBranch(@Query("branchID") long branchID);

    @GET(ApiConstant.GET_ALL_HOMEWORK_BY_ID)
    Call<HomeworkByIdData> GetHomeworkByHWID(@Query("hwID") long hwID);

    @POST(ApiConstant.REMOVE_HOMEWORK)
    Call<CommonModel> RemoveHomework(@Query("hwID") long hwID, @Query("lastupdatedby") String lastupdatedby);

    @GET(ApiConstant.GET_ANSWERSHEET)
    Call<AnswerSheetData> GetAllAnsSheetByTest(@Query("testID") long testID);

    @POST(ApiConstant.REMOVE_REMINDER)
    Call<CommonModel> RemoveReminder(@Query("reminderID") long reminderID, @Query("lastupdatedby") String lastupdatedby);

    @POST(ApiConstant.REMOVE_TODO)
    Call<CommonModel> RemoveTodo(@Query("todoID") long todoID, @Query("lastupdatedby") String lastupdatedby);

    @POST(ApiConstant.REMOVE_TEST_SCHEDULE_PAPER)
    Call<CommonModel> RemoveTest(@Query("testID") long testID, @Query("lastupdatedby") String lastupdatedby, @Query("removePaper") Boolean removePaper);

}

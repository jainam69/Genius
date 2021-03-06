package com.example.genius.API;

public class ApiConstant {

    //public static final String BASE_URL = "http://192.168.29.212:45455/api/";
    public static final String BASE_URL = "http://43.241.39.136:86/api/";
    //public static final String BASE_URL = "http://43.241.39.136:84/api/";

    public static final String VALIDATE_USER = "user/v1/ValidateUser";
    public static final String FORGOT_PASSWORD = "user/v1/CheckUserName";
    public static final String GET_PERMISSION = "user/v1/UserPermission";
    public static final String CHANGE_PASSWORD = "user/v1/ChangePassword";
    public static final String UPDATE_PROFILE = "profile/v1/UpdateProfile";
    public static final String GET_STAFF_BY_ID = "profile/v1/GetStaffByID";

    public static final String GET_ALL_STANDARD = "standard/v1/GetAllStandards";

    public static final String GET_ALL_SCHOOL = "school/v1/GetAllSchools";
    public static final String SCHOOL_MAINTENANCE = "school/v1/SchoolMaintenance";
    public static final String REMOVE_SCHOOL = "school/v1/RemoveSchool";

    public static final String GET_ALL_STAFF = "staff/v1/GetAllStaff";
    public static final String STAFF_MAINTENANCE = "staff/v1/StaffMaintenance";
    public static final String REMOVE_STAFF = "staff/v1/RemoveStaff";
    public static final String GET_ALL_STUDENT_WITHOUT_CONTENT = "student/v1/GetAllStudentWithoutContent";
    public static final String GET_ALL_ACTIVE_STUDENT_WITHOUT_CONTENT = "student/v1/GetAllActiveStudentWithoutContent";
    public static final String GET_ALL_INACTIVE_STUDENT_WITHOUT_CONTENT = "student/v1/GetAllInActiveStudentWithoutContent";

    public static final String STUDENT_MAINTENANCE = "student/v1/StudentMaintenance";
    public static final String GET_ALL_MOBILE_NOTIFICATION = "notification/v1/GetMobileNotification";
    public static final String NOTIFICATION_MAINTENANCE = "notification/v1/NotificationMaintenance";
    public static final String REMOVE_NOTIFICATION = "notification/v1/RemoveNotification";
    public static final String CHECK_PACKAGE_STUDENT_LIMIT = "student/v1/CheckPackage";

	public static final String LIVE_VIDEO_MAINTENANCE = "link/v1/LiveVideoMaintenance";
    public static final String GET_LIVE_VIDEO_LINKS_BRANCH = "link/v1/GetLiveVideoLinksByBranch";
    public static final String REMOVE_LINK = "link/v1/RemoveLink";

	public static final String YOUTUBE_MAINTENANCE = "link/v1/YouTubeVideoMaintenance";
    public static final String GET_YOU_TUBE_LINKS_BRANCH = "link/v1/GetYouTubeVideoLinksByBranch";

    public static final String BANNER_MAINTENANCE = "banner/v1/BannerMaintenance";
    public static final String GET_ALL_BANNER_BRANCH = "banner/v1/GetAllBannerByBranch";
    public static final String GET_ALL_BANNER_BRANCH_TYPE = "banner/v1/GetAllBannerByBranchAndType";
    public static final String REMOVE_BANNER = "banner/v1/RemoveBanner";

    public static final String GALLRY_IMAGE_MAINTENANCE = "gallery/v1/GalleryMaintenance";
    public static final String GET_ALL_IMAGES_BRANCH = "gallery/v1/GetAllGalleryImagesByBranch";
    public static final String REMOVE_IMAGE = "gallery/v1/RemoveGallery";
    public static final String GET_ALL_VIDEO_BRANCH = "gallery/v1/GetAllGalleryVideoByBranch";

    public static final String GET_ALL_USER_DDL = "user/v1/GetAllUsersddl";

    public static final String PRACTICE_PAPER_MAINTENANCE = "paper/v1/PaperMaintenance";
    public static final String GET_ALL_PRACTICE_PAPER_WITHOUT_CONTENT = "paper/v1/GetAllPaperWithoutContent";
    public static final String GET_PRACTICE_PAPER_BY_PAPERID = "paper/v1/GetPaperByPaperID";
    public static final String REMOVE_PAPER = "paper/v1/RemovePaper";

    public static final String GET_ALL_MOBILE_LIBRARY = "library/v1/GetAllMobileLibrary";
    public static final String REMOVE_LIBRARY = "library/v1/RemoveLibrary";

    public static final String GET_STUDENT_ATTENDANCE_LIST = "attendance/v1/GetAllStudentForAttendance";
    public static final String ATTENDANCE_MAINTENANCE = "attendance/v1/AttendanceMaintenance";
    public static final String GET_ATTENDANCE_LIST = "attendance/v1/GetAllAttendanceByBranch";
    public static final String REMOVE_ATTENDANCE = "attendance/v1/RemoveAttendance";
    public static final String GET_ATTENDANCE_BY_ID = "attendance/v1/GetAttendanceByID";

    public static final String TESTSCHEDULE_MAINTENANCE = "test/v1/TestMaintenance";
    public static final String GET_TESTSCHEDULE_BY_BRANCH_API = "test/v1/GetAllTestByBranchAPI";
    public static final String NEW_UPLOAD_PAPER_MAINTENANCE = "test/v1/TestPaperMaintenance";
    public static final String REMOVE_TEST_SCHEDULE_PAPER = "test/v1/RemoveTest";
    public static final String GET_EDIT_PAPER_DATA = "test/v1/GetAllTestPapaerWithoutContentByTest";
    public static final String GET_ANSWERSHEET = "test/v1/GetAnswerSheetdata";
    public static final String GET_ALL_HOMEWORK_WITHOUT_CONTENT = "homework/v1/GetAllHomeworkWithoutContentByBranch";
    public static final String REMOVE_HOMEWORK = "homework/v1/RemoveHomework";
    public static final String NEW_HOMEWORK_MAINTENANCE = "homework/v1/HomeworkMaintenance";

	public static final String REMINDER_MAINTENANCE = "reminder/v1/ReminderMaintenance";
    public static final String GET_ALL_REMINDER_BY_BRANCH_USER = "reminder/v1/GetAllReminderByBranchAndUser";
    public static final String REMOVE_REMINDER = "reminder/v1/RemoveReminder";

    public static final String TODO_MAINTENANCE = "todo/v1/ToDoMaintenance";
    public static final String GET_ALL_TODO_BY_BRANCH_WITHOUT_CONTENT = "todo/v1/GetAllToDoWithoutContentByBranch";
    public static final String GET_ALL_TODO_BY_BRANCH_WITHOUT_CONTENT_BRANCH = "todo/v1/GetAllToDoWithoutContentByBranchAndUser";
    public static final String GET_TODO_BY_ID = "todo/v1/GetToDoByHWID";
    public static final String REMOVE_TODO = "todo/v1/RemoveToDo";

    public static final String FEES_MAINTENANCE = "FeesStructure/v1/FeesMaintenance";
    public static final String GET_ALL_FEES_BRANCH = "FeesStructure/v1/GetFeesByBranchID";
    public static final String REMOVE_FEES = "FeesStructure/v1/RemoveFees";

    public static final String GET_ALL_CATEGORY_BRANCH = "NewLibrary/v1/GetAllCategory";
    public static final String OldLibraryMaintenance = "library/v1/LibraryMaintenance";
    public static final String GET_LIBRARY_APPROVAL_LIST = "library/v1/GetAllLibraryApproval";
    public static final String GET_LIBRARY_APPROVAL_LIST_BRANCH = "library/v1/GetLibraryApprovalByBranch";
    public static final String LIBRARY_APPROVAL_MAINTENANCE = "library/v1/LibraryApprovalMaintenance";

    public static final String GET_TEST_MARKS_DATE = "Marks/v1/GetTestDatesByBatch";
    public static final String UPDATE_HOMEWORK_CHECKING = "homework/v1/Updatehomeworkdetails";
    public static final String GET_HOMEWORK_CHECKING_LIST = "homework/v1/GetStudentHomeworkChecking";
    public static final String UPDATE_TEST_PAPER_CHECKING = "test/v1/UpdateAnsdetails";
    public static final String GET_TEST_DETAILS = "Marks/v1/GetTestDetails";
    public static final String GET_STUDENT_DEATILS = "Marks/v1/GetStudentByStd";
    public static final String MARKS_MAITENANCE = "Marks/v1/MarksMaintenance";
    public static final String GET_ALL_SUBJECT_BY_TESTDATE = "subject/v1/GetAllSubjectsByTestDate";
    public static final String GET_ALL_STUDENT_ACHIEVE_MARK = "Marks/v1/GetAllAchieveMarks";
    public static final String UPDATE_ACHIEVE_MARKS = "Marks/v1/UpdateMarksDetails";
    public static final String DOWNLOAD_STUDENT_HOMEWORK = "homework/v1/DownloadZipFile";
    public static final String DOWNLOAD_STUDENT_TEST_PAPER = "test/v1/DownloadZipFile";

    //Branch Class
    public static final String BRANCH_CLASS_MAITENANCE = "branchclass/v1/BranchClassMaintenance";
    public static final String GET_BRANCH_CLASS_BRANCHID = "branchclass/v1/GetAllBranchClassByBranchID";
    public static final String BRANCH_CLASS_REMOVE = "branchclass/v1/RemoveBranchClass";
    public static final String GET_BRANCH_CLASS_SPINNER = "branchclass/v1/GetAllClassDDL";
    public static final String GET_ALL_CLASS_BY_COURSE = "branchclass/v1/GetAllClassByCourse";
    public static final String CHECK_CLASS_DETAIL = "branchclass/v1/CheckClassDetail";

    //Branch Course
    public static final String BRANCH_COURCE_MAITENANCE = "branchcource/v1/BranchCourseMaintenance";
    public static final String GET_BRANCH_COURCE_BRANCHCOURCE_BY_ID = "branchcource/v1/GetBranchCourseByBranchCourseID";
    public static final String GET_BRANCH_COURCE_ALL = "branchcource/v1/GetAllCourse";
    public static final String BRANCH_COURCE_REMOVE = "branchcource/v1/RemoveBranchCourse";
    public static final String GET_BRANCH_COURSE_SPINNER = "branchcource/v1/GetAllCourseDDL";
    public static final String GET_ALL_COURSE_DDL = "branchcource/v1/GetAllCourseDDL";

    //Branch Subject
    public static final String BRANCH_SUBJECT_MAITENANCE = "branchsubject/v1/BranchSubjectMaintenance";
    public static final String GET_BRANCH_SUBJECT_BRANCHID = "branchsubject/v1/GetAllBranchSubjectByBranchID";
    public static final String BRANCH_SUBJECT_REMOVE = "branchsubject/v1/RemoveSubjectDetail";
    public static final String GET_ALL_SUBJECT_DDL = "branchsubject/v1/GetAllSubjectDDL";
    public static final String GET_LIBRARY_SUBJECT_DDL = "branchsubject/v1/GetLibrarySubjectDDL";
    public static final String GET_ALL_SUBJECT_BY_COURSE = "branchsubject/v1/GetAllSubjectByCourse";
    public static final String CHECK_SUBJECT_DETAIL = "branchsubject/v1/Check_SubjectDetail";

    public static final String FACULTY_REMOVE = "faculty/v1/RemoveFaculty";
    public static final String GET_FACULTY_LIST = "faculty/v1/GetFacultyByBranchID";
    public static final String FACULTY_MAINTENANCE = "faculty/v1/FacultyMaintenance";

    public static final String AnnouncementMaintenance = "announcement/v1/AnnouncementMaintenance";
    public static final String GET_ALL_ANNOUNCEMENT = "announcement/v1/GetAllAnnouncement";

    public static final String GET_CIRCULAR = "circular/v1/GetAllCircular";

    public static final String GET_ALL_BATCH_LIST = "batch/v1/GetAllBatches";
    public static final String BATCH_MAINTAINANCE = "batch/v1/BatchMaintenance";
    public static final String REMOVE_BATCH = "batch/v1/RemoveBatch";

    public static final String GET_ALL_UPI_DETAILS = "user/v1/GetAllUPIs";
    public static final String UPI_MAINTENANCE = "user/v1/UPIMaintenance";
    public static final String REMOVE_UPI_DETAIL = "user/v1/RemoveUPI";

    public static final String GET_ALL_ROLES = "Role/v1/GetAllRolesByBranch";
    public static final String ROLE_MAINTENANCE = "Role/v1/RoleMaintenance";
    public static final String REMOVE_ROLE = "Role/v1/RemoveRole";

    public static final String GET_ALL_ROLE_RIGHTS = "RoleRights/v1/GetAllRightsbyBranch";
    public static final String GET_PAGE_ROLE_RIGHT_LIST = "RoleRights/v1/GetPageListbyBranchID";
    public static final String ROLE_RIGHTS_MAINTENANCE = "RoleRights/v1/RoleRightsMaintenance";
    public static final String REMOVE_ROLE_RIGHTS = "RoleRights/v1/RemoveRoleRights";

    public static final String GET_USER_RIGHT_ROLE_LIST = "UserRights/v1/UserRightUniqueData";
    public static final String USER_RIGHTS_MAINTENANCE = "UserRights/v1/UserRightsMaintenance";
    public static final String GET_ALL_USER_BY_BRANCH = "UserRights/v1/GetAllStaffUserbyBranch";
    public static final String GET_ALL_USER_RIGHTS_LIST = "UserRights/v1/GetAllUserRightsbyBranchId";
    public static final String REMOVE_USER_RIGHTS = "UserRights/v1/RemoveUserRights";
}

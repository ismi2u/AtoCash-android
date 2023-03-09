package com.atocash.utils

import com.atocash.BuildConfig


/**
 * Created by geniuS on 11/27/2019.
 */
class Keys {

    object StatusType {
        const val PENDING = "Pending"
        const val APPROVED = "Approved"
        const val REJECTED = "Rejected"
        const val INITIATING = "Initiating"
        const val IN_REVIEW = "In Review"
    }

    object LoginType {
        const val ATOMINOS_ADMIN = "AtominosAdmin"
        const val ADMIN = "Admin"

        const val EMPLOYEE = "User"

        const val FINANCE_MANAGER = "Finmgr"
        const val MANAGER = "Manager"
        const val ACC_PAYABLE = "AccPayable"
    }

    object UserData {
        const val IS_ALSO_EMPLOYEE = "isAlsoEmployee"
        const val HAS_MULTIPLE_LOGINS = "hasMultipleLogins"
        const val USER_ROLE = "role"
        const val TOKEN = "token"
        const val AUTH = "Authorization"
        const val FIRST_NAME = "firstName"
        const val LAST_NAME = "lastName"
        const val ID = "empId"
        const val EMPID = "employeeId"
        const val EMAIL = "email"
        const val CURRENCY_CODE = "currencyCode"
        const val CURRENCY_ID = "currencyId"
    }

    object Api {
        var BASE_URL = BuildConfig.PROD_SERVER_URL
//        var BASE_URL = BuildConfig.DEV_SERVER_URL
//        var BASE_URL = BuildConfig.STAGE_SERVER_URL

        val BASE_PATH = "$BASE_URL/api/"
        var IMAGE_PREFIX = BASE_URL

        const val STATUS_FAIL = "Failure"
        const val STATUS_SUCCESS = "Success"

        const val LOGIN = "Account/Login"

        const val POST_DEPARTMENT = "Departments/PostDepartment"
        const val PUT_DEPARTMENT = "Departments/PutDepartment"
        const val DELETE_DEPARTMENT = "Departments/DeleteDepartment"

        const val POST_COST_CENTER = "CostCenters/PostCostCenter"
        const val PUT_COST_CENTER = "CostCenters/PutCostCenter"
        const val DELETE_COST_CENTER = "CostCenters/DeleteCostCenter"

        const val POST_EXPENSE_TYPES = "ExpenseTypes/PostExpenseType"
        const val PUT_EXPENSE_TYPES = "ExpenseTypes/PutExpenseType"

        const val GET_COST_CENTER = "CostCenters/GetCostCenters"
        const val GET_DEPARTMENTS = "Departments/GetDepartments"

        const val  GET_EXPENSE_TYPES = "ExpenseTypes/GetExpenseTypes"
        const val  DELETE_EXPENSE_TYPES = "ExpenseTypes/GetExpenseTypes"

        const val GET_EXPENSE_TYPES_FOR_NON_PROJECT = "ExpenseCategories/GetSelectedExpenseCategoriesForDropdown"
        const val GET_EXPENSE_TYPES_FOR_NON_PROJECT_CATEGORY_ID = "ExpenseTypes/ExpenseTypesForExpenseCategoryId"
        const val GET_VAT_PERCENTAGE = "VATRate/GetVATPercentage/"

        const val GET_DROP_DOWN_STATUS = "StatusTypes/StatusTypesForDropdown"
        const val GET_DROP_DOWN_COST_CENTER = "CostCenters/CostCentersForDropdown"
        const val GET_DROP_DOWN_CURRENCY_TYPES = "CurrencyTypes/CurrencyTypesForDropdown"
        const val GET_DROP_DOWN_APPROVAL_GROUPS = "ApprovalGroups/ApprovalGroupsForDropdown"
        const val GET_DROP_DOWN_JOB_ROLES = "JobRoles/JobRolesForDropdown"
        const val GET_DROP_DOWN_DEPARTMENT = "Departments/DepartmentsForDropdown"
        const val GET_DROP_DOWN_EMPLOYMENT_TYPES = "EmploymentTypes"

        const val GET_EMPLOYMENT_TYPES = "EmploymentTypes"
        const val GET_EMPLOYMENT_TYPE = "EmploymentTypes"
        const val POST_EMPLOYMENT_TYPES = "EmploymentTypes"
        const val PUT_EMPLOYMENT_TYPES = "EmploymentTypes"
        const val DELETE_EMPLOYMENT_TYPES = "EmploymentTypes"

        const val GET_EMPLOYEES = "Employees/GetEmployees"
        const val GET_EMPLOYEE = "Employees/GetEmployee"
        const val GET_EMPLOYEES_FOR_DROP_DOWN = "Employees/EmployeesForDropdown"
        const val PUT_EMPLOYEE = "Employees/PutEmployee"
        const val POST_EMPLOYEE = "Employees/PostEmployee"
        const val DELETE_EMPLOYEE = "Employees/DeleteEmployee"

        const val GET_PROJECTS = "Projects/GetProjects"
        const val GET_PROJECT = "Projects/GetProject"
        const val GET_PROJECTS_FOR_DROP_DOWN = "Projects/ProjectsForDropDown"
        const val PUT_PROJECT = "Projects/PutProject"
        const val POST_PROJECT = "Projects/PostProject"
        const val DELETE_PROJECT = "Projects/DeleteProject"
        const val GET_EMPLOYEE_ASSIGNED_PROJECTS = "Projects/GetEmployeeAssignedProjects"

        const val POST_PROJECT_MANAGEMENT = "ProjectManagement/PostProjectManagement"

        const val GET_SUB_PROJECTS = "SubProjects/GetSubProjects"
        const val GET_SUB_PROJECT = "SubProjects/GetSubProject"
        const val GET_SUB_PROJECTS_FOR_DROP_DOWN = "SubProjects/SubProjectsForDropdown"
        const val GET_SUB_PROJECTS_FOR_PROJECTS = "SubProjects/GetSubProjectsForProjects"
        const val PUT_SUB_PROJECT = "SubProjects/PutSubProject"
        const val POST_SUB_PROJECT = "SubProjects/PostSubProject"
        const val DELETE_SUB_PROJECT = "SubProjects/DeleteSubProject"

        const val GET_WORK_TASKS = "WorkTasks/GetWorkTasks"
        const val GET_WORK_TASK = "WorkTasks/GetWorkTasks"
        const val GET_WORK_TASKS_FOR_DROP_DOWN = "WorkTasks/GetWorkTasksForDropdown"
        const val GET_WORK_TASKS_FOR_SUB_PROJECTS = "WorkTasks/GetWorkTasksForSubProjects"
        const val PUT_WORK_TASK = "WorkTasks/PutWorkTask"
        const val POST_WORK_TASK = "WorkTasks/PostWorkTask"
        const val DELETE_WORK_TASK = "WorkTasks/DeleteWorkTask"

        const val GET_USERS = "Administration/ListUsers"
        const val GET_USER = "Administration/GetUserByUserId"
        const val DELETE_USER = "Administration/DeleteUser"
        const val PUT_USER = "Administration/EditUser"

        const val GET_USER_ROLES = "Administration/ListRoles"
        const val DELETE_USER_ROLES = "Administration/DeleteRole"
        const val PUT_USER_ROLES = "Administration/EditRole"
        const val POST_USER_ROLES = "Administration/CreateRole"

        const val GET_JOB_ROLES = "JobRoles/GetRoles"
        const val DELETE_JOB_ROLES = "Administration/DeleteRole"
        const val PUT_JOB_ROLES = "Administration/EditRole"
        const val POST_JOB_ROLES = "Administration/CreateRole"
        const val ASSIGN_ROLES = "Administration/AssignRole"

        const val GET_APPROVAL_GROUPS = "ApprovalGroups/GetApprovalGroups"
        const val GET_APPROVAL_GROUP = "ApprovalGroups/GetApprovalGroup"
        const val GET_APPROVAL_GROUPS_FOR_DROP_DOWN = "ApprovalGroups/ApprovalGroupsForDropDown"
        const val PUT_APPROVAL_GROUP = "ApprovalGroups/PutApprovalGroup"
        const val POST_APPROVAL_GROUP = "ApprovalGroups/PostApprovalGroup"
        const val DELETE_APPROVAL_GROUP = "ApprovalGroups/DeleteApprovalGroup"

        const val GET_APPROVAL_LEVELS = "ApprovalLevels"
        const val GET_APPROVAL_LEVEL = "ApprovalLevels"
        const val POST_APPROVAL_LEVEL = "ApprovalLevels"
        const val PUT_APPROVAL_LEVEL = "ApprovalLevels"
        const val DELETE_APPROVAL_LEVEL = "ApprovalLevels"

        const val GET_APPROVAL_ROLE_MAPS = "ApprovalRoleMaps"
        const val GET_APPROVAL_ROLE_MAP = "ApprovalRoleMaps"
        const val POST_APPROVAL_ROLE_MAP = "ApprovalRoleMaps"
        const val PUT_APPROVAL_ROLE_MAP = "ApprovalRoleMaps"
        const val DELETE_APPROVAL_ROLE_MAP = "ApprovalRoleMaps"

        const val GET_APPROVAL_STATUS_TYPES = "ApprovalStatusTypes"
        const val GET_APPROVAL_STATUS_TYPE = "ApprovalStatusTypes"
        const val POST_APPROVAL_STATUS_TYPE = "ApprovalStatusTypes"
        const val PUT_APPROVAL_STATUS_TYPE = "ApprovalStatusTypes"
        const val DELETE_APPROVAL_STATUS_TYPE = "ApprovalStatusTypes"

        const val GET_CURRENCY_LIST_FOR_DROP_DOWN = "CurrencyTypes/CurrencyTypesForDropDown"
        const val GET_CURRENCY_LIST = "CurrencyTypes/GetCurrencyTypes"
        const val GET_CURRENCY = "CurrencyTypes/GetCurrencyType"
        const val POST_CURRENCY = "CurrencyTypes/PostCurrencyType"
        const val PUT_CURRENCY = "CurrencyTypes/PutCurrencyType"
        const val DELETE_CURRENCY = "CurrencyTypes/DeleteCurrencyType"

        const val GET_PETTY_CASH_REQUESTS = "PettyCashRequests/GetPettyCashRequests"
//        const val GET_PETTY_CASH_REQUEST = "CashAdvanceStatusTrackers/ApprovalFlowForRequest"
        const val GET_PETTY_CASH_REQUEST = "CashAdvanceRequests/GetCashAdvanceRequest"
//        const val GET_PETTY_CASH_REQUEST_RAISED_FOR_EMPLOYEE = "PettyCashRequests/GetPettyCashRequestRaisedForEmployee"
        const val GET_PETTY_CASH_REQUEST_RAISED_FOR_EMPLOYEE = "CashAdvanceRequests/GetCashAdvanceRequestRaisedForEmployee"
        const val GET_PETTY_CASH_REQUEST_COUNT_RAISED_BY_EMPLOYEE = "PettyCashRequests/CountAllPettyCashRequestRaisedByEmployee"
        const val GET_PETTY_CASH_REQUEST_PENDING_FOR_ALL = "PettyCashRequests/GetPettyCashReqInPendingForAll"
//        const val PUT_PETTY_CASH_REQUEST = "CashAdvanceRequests/PostCashAdvanceRequest"
        const val PUT_PETTY_CASH_REQUEST = "CashAdvanceRequests/PutCashAdvanceRequest"
//        const val POST_PETTY_CASH_REQUEST = "PettyCashRequests/PostPettyCashRequest"
        const val POST_PETTY_CASH_REQUEST = "CashAdvanceRequests/PostCashAdvanceRequest"
//        const val DELETE_PETTY_CASH_REQUEST = "PettyCashRequests/DeletePettyCashRequest"
        const val DELETE_PETTY_CASH_REQUEST = "CashAdvanceRequests/DeleteCashAdvanceRequest"

//        const val INBOX_CASH_ADVANCE = "ClaimApprovalStatusTrackers/ApprovalsPendingForApprover"
        const val INBOX_CASH_ADVANCE = "CashAdvanceStatusTrackers/ApprovalsPendingForApprover"
        const val INBOX_CASH_ADVANCE_COUNT = "CashAdvanceRequests/CountAllCashAdvanceRequestRaisedByEmployee"
        const val INBOX_EXPENSE_REIMBURSE = "ExpenseReimburseStatusTrackers/ApprovalsPendingForApprover"
        const val INBOX_TRAVEL_REQUEST = "TravelApprovalStatusTrackers/ApprovalsPendingForApprover"

        const val DELETE_EXPENSE_REIMBURSE_REQUEST = "ExpenseReimburseRequests/DeleteExpenseReimburseRequest"
        const val GET_EXPENSE_REIMBURSE_COUNT_RAISED_BY_EMPLOYEE = "ExpenseReimburseRequests/CountAllExpenseReimburseRequestRaisedByEmployee"
        const val GET_EXPENSE_REIMBURSE_RAISED_FOR_EMPLOYEE = "ExpenseReimburseRequests/GetExpenseReimburseRequestRaisedForEmployee"
        const val GET_EXPENSE_REIMBURSE_REQUEST = "ExpenseReimburseRequests/GetExpenseReimburseRequest"

        const val GET_EXPENSE_REIMBURSE_SUB_CLAIMS = "ExpenseSubClaims/GetExpenseSubClaimsByExpenseId"
        const val GET_EXPENSE_REIMBURSE_SUB_CLAIMS_DOCUMENTS = "ExpenseReimburseRequests/GetDocumentsBySubClaimsId"
//        const val GET_EMP_CUR_BAL_AND_CASH_IN_HAND = "EmpCurrentPettyCashBalances/GetEmpMaxlimitcurBalAndCashInHandStatus"
        const val GET_EMP_CUR_BAL_AND_CASH_IN_HAND = "EmpCurrentCashAdvanceBalances/GetEmpMaxlimitcurBalAndCashInHandStatus"

        const val GET_TRAVEL_EXPENSE_COUNT_RAISED_BY_EMPLOYEE = "TravelApprovalRequests/CountAllTravelRequestRaisedByEmployee"

//        const val CURRENT_PETTY_CASH_BALANCE = "EmpCurrentPettyCashBalances/GetEmpCurrentPettyCashBalance"
        const val CURRENT_PETTY_CASH_BALANCE = "EmpCurrentCashAdvanceBalances/GetEmpCashBalanceVsAdvanced"
        const val CURRENT_PETTY_CASH_BALANCE_ADVANCED = "EmpCurrentPettyCashBalances/GetEmpCashBalanceVsAdvanced"

//        const val APPROVE_REJECT_CASH_ADVANCE = "ClaimApprovalStatusTrackers/PutClaimApprovalStatusTracker"
        const val APPROVE_REJECT_CASH_ADVANCE = "CashAdvanceStatusTrackers/PutCashAdvanceStatusTracker"
        const val APPROVE_REJECT_EXPENSE_REIMBURSE = "ExpenseReimburseStatusTrackers/PutExpenseReimburseStatusTracker"
        const val APPROVE_REJECT_TRAVEL_REQUEST = "TravelApprovalStatusTrackers/PutTravelApprovalStatusTracker"

//        const val APPROVAL_FLOW_FOR_REQUEST = "ClaimApprovalStatusTrackers/ApprovalFlowForRequest"
        const val APPROVAL_FLOW_FOR_REQUEST = "CashAdvanceStatusTrackers/ApprovalFlowForRequest"
        const val APPROVAL_FLOW_FOR_EXPENSE_REIMBURSE_REQUEST = "ExpenseReimburseStatusTrackers/ApprovalFlowForRequest"
//        const val APPROVE_EXPENSE_REIMBURSE = "ExpenseReimburseStatusTrackers/ApprovalsPendingForApprover"
//        const val APPROVE_TRAVEL_REQUEST = "TravelApprovalStatusTrackers/ApprovalsPendingForApprover"

        const val POST_DOCUMENTS = "ExpenseReimburseRequests/PostDocuments"

        const val POST_EXPENSE_REIMBURSE = "ExpenseReimburseRequests/PostExpenseReimburseRequest"

        const val GET_TRAVEL_REQUEST_FOR_EMPLOYEE = "TravelApprovalRequests/GetTravelApprovalRequestRaisedForEmployee"
        const val COUNT_TRAVEL_REQUEST_BY_EMPLOYEE = "TravelApprovalRequests/CountAllTravelRequestsRaisedByEmployee"
        const val GET_TRAVEL_REQUEST_DETAIL = "TravelApprovalRequests/GetTravelApprovalRequest"
        const val GET_TRAVEL_REQUESTS = "TravelApprovalRequests/GetTravelApprovalRequests"
        const val GET_TRAVEL_REQUEST_PENDING_FOR_ALL = "TravelApprovalRequests/GetTravelReqInPendingForAll"
        const val PUT_TRAVEL_REQUEST = "TravelApprovalRequests/PutTravelApprovalRequest"
        const val POST_TRAVEL_REQUEST = "TravelApprovalRequests/PostTravelApprovalRequest"
        const val DELETE_TRAVEL_REQUEST = "TravelApprovalRequests/DeleteTravelApprovalRequest"

//        const val TRAVEL_APPROVAL_FLOW_FOR_REQUEST = "TravelApprovalStatusTrackers/ApprovalFlowForTravelRequest"
        const val TRAVEL_APPROVAL_FLOW_FOR_REQUEST = "TravelApprovalStatusTrackers/ApprovalFlowForRequest"

        const val GET_BUSINESS_TYPE_FOR_DROP_DOWN = "BusinessTypes/BusinessTypesForDropdown"
        const val GET_BUSINESS_UNITS_FOR_DROP_DOWN = "BusinessUnits/BusinessUnitsByBizTypeIdAndEmpIdForDropDown"
        const val GET_BUSINESS_UNIT_DETAILS = "BusinessUnits/GetBusinessUnit"
        const val VENDORS_FOR_DROP_DOWN = "Vendors/VendorsForDropdown"


        const val email = "email"
        const val password = "password"
        const val rememberMe = "rememberMe"
        const val id = "id"
        const val costCenterCode = "costCenterCode"
        const val costCenterDesc = "costCenterDesc"
    }

    companion object {
        const val REQ_CODE_VERSION_UPDATE = 530
        const val PREFS_NAME = BuildConfig.APPLICATION_ID + "_prefs"
        const val EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

        const val IS_LOGGED_IN = "is_logged_in"
        const val LOGIN_TYPE = "login_type"
        const val IS_FIRST_TIME = "is_first_time"
        const val IS_INTRO_SHOWN = "is_intro_shown"
        const val LANGUAGE_PREF = "chosen_language"
        const val IS_SUB_DOMAIN = "api_base"

        const val BASE_URL = "base_url"

        const val SPLASH_DURATION: Long = 2000
        const val SPLASH_ANIM_DURATION: Long = SPLASH_DURATION - 500
        const val INTRO_PAGES = 4
        const val REQUEST_TIMEOUT = 60 * 1000
    }

    object RecyclerItem {
        const val ITEM = 0 //layout normal view
        const val EMPTY = 1 //empty layout
        const val LOADING = 2 //load more

        const val PAGE_START = 0
        const val TOTAL_PAGES = 999
    }

    object Fragments {
        const val FRAGMENT_HOME = "Home"
    }

    object Approval {
        const val LEVEL = "ApprovalLevel"
        const val GROUP = "ApprovalGroup"
        const val ROLE_MAP = "ApprovalRoleMap"
        const val STATUS = "ApprovalStatus"
    }

    object IntentData {
        const val PURPOSE = "purpose"
        const val IS_EDIT = "is_editing"
        const val IS_COPY = "is_copy"
        const val IS_VIEW = "view"
        const val FROM = "from"
        const val TO = "to"
        const val URL = "url"
    }

    object IntentHelper {
        const val OPEN_FOR_RESULT = 9
        const val OPEN_CAMERA = 100
        const val OPEN_GALLERY_INTENT = 101
        const val SELECT_CONTACT = 102
        const val PICK_FILE = 103

        const val CIRCULAR_REVEAL_X = "reveal_x"
        const val CIRCULAR_REVEAL_Y = "reveal_y"
        const val CIRCULAR_TRANSITION = "reveal_transition"

        const val FILE_FORMAT_TEXT = "text/plain"
        const val FILE_FORMAT_IMAGE = "image/*"
        const val FILE_FORMAT_VIDEO = "video/*"
        const val FILE_FORMAT_WORD = "application/msword"
        const val FILE_FORMAT_WORD_X =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        const val FILE_FORMAT_POWERPOINT = "application/vnd.ms-powerpoint"
        const val FILE_FORMAT_EXCEL = "application/vnd.ms-excel"
        const val FILE_FORMAT_PDF = "application/pdf"

        var mimeTypes = arrayOf(
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",  // .doc & .docx
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",  // .xls & .xlsx
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",  // .ppt & .pptx
            "application/vnd.ms-excel", "text/plain", "application/pdf", "application/zip"
        )
    }

}
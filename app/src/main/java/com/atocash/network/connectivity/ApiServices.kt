package com.atocash.network.connectivity

import com.atocash.activities.admin.travelRequest.ManageTravelRequestActivity
import com.atocash.common.fragments.inbox.InboxFragment
import com.atocash.network.request.BusinessUnitsDropDownRequest
import com.atocash.network.request.LoginRequest
import com.atocash.network.request.TravelRequestDto
import com.atocash.network.response.*
import com.atocash.network.response.dashboard.CashAdvanceCountResponseData
import com.atocash.utils.Keys
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface ApiServices {

//    @Multipart
//    @POST(ApiConstants.VIDEO_END_POINT)
//    fun uploadVideo(@Header(NetworkConstants.KEY_AUTHORIZATION) authToken: String, @Query(NetworkConstants.KEY_LOCALE) locale: String,
//                    @Part file: MultipartBody.Part, @Part channel: MultipartBody.Part, @Part chapterId: MultipartBody.Part,
//                    @Part description: MultipartBody.Part, @Part name: MultipartBody.Part, @Part price: MultipartBody.Part): Call<ResponseData<VideoResponse>>
//
//    @Multipart
//    @POST(ApiConstants.POST_ADS_REQUESTS_END_POINT)
//    fun postAdvertisementRequest(@Header(NetworkConstants.KEY_AUTHORIZATION) authToken: String, @Part adFile: MultipartBody.Part,
//                                 @Part tutorId: MultipartBody.Part, @Part adTitle: MultipartBody.Part,
//                                 @Part adDescription: MultipartBody.Part, @Part adAmount: MultipartBody.Part, @Part currencyCode: MultipartBody.Part): Call<ResponseData<ErrorResponse>>

//    @POST(ApiConstants.LOGIN_END_POINT)
//    fun loginUser(@Query(NetworkConstants.KEY_LOCALE) locale: String, @Body jsonObject: JsonObject): Call<ResponseData<com.blocedu.android.beans.loginresponse.Data>>

//    @POST(ApiConstants.BUY_VIP_END_POINT + "/{id}")
//    fun buyVipPackage(@Header(NetworkConstants.KEY_AUTHORIZATION) authToken: String, @Path(value = "id") courseId: Long, @Query(NetworkConstants.KEY_LOCALE) locale: String): Call<ResponseData<ErrorResponse>>

    /*@Header(Keys.UserData.AUTH) authToken: String, */

    @POST(Keys.Api.LOGIN)
    fun doLogin(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET(Keys.Api.GET_COST_CENTER)
    fun getCostCenters(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<CostCenterResponse>>

    @PUT(Keys.Api.PUT_COST_CENTER + "/{id}")
    fun putCostCenter(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int,
        @Body loginRequest: JsonObject
    ): Call<BaseResponse>

    @DELETE(Keys.Api.DELETE_COST_CENTER + "/{id}")
    fun deleteCostCenter(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<BaseResponse>

    @POST(Keys.Api.POST_COST_CENTER)
    fun postCostCenter(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body loginRequest: JsonObject
    ): Call<BaseResponse>

    /*Department ----------------*/
    @GET(Keys.Api.GET_DEPARTMENTS)
    fun getAllDepartmentsList(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<DepartmentResponse>>

    @PUT(Keys.Api.PUT_DEPARTMENT + "/{id}")
    fun putDepartment(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int,
        @Body jsonObject: JsonObject
    ): Call<BaseResponse>

    @POST(Keys.Api.POST_DEPARTMENT)
    fun postDepartment(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body jsonObject: JsonObject
    ): Call<BaseResponse>

    @DELETE(Keys.Api.DELETE_DEPARTMENT + "/{id}")
    fun deleteDepartment(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<BaseResponse>
    /*Department ends----------------------------------*/

    @GET(Keys.Api.GET_EXPENSE_TYPES)
    fun getExpenseTypes(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<ExpenseTypeResponse>>

    @GET(Keys.Api.GET_EXPENSE_TYPES_FOR_NON_PROJECT)
    fun getExpenseTypesForNonProject(
        @Header(Keys.UserData.AUTH) authToken: String
    ): Call<ArrayList<ExpenseTypeNonProjectResponse>>

    @GET(Keys.Api.GET_EXPENSE_TYPES_FOR_NON_PROJECT_CATEGORY_ID + "/{id}")
    fun getExpenseTypesForNonProjectCategoryId(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ArrayList<ExpenseTypeResponse>>

    @GET(Keys.Api.GET_VAT_PERCENTAGE)
    fun getVatPercentage(
        @Header(Keys.UserData.AUTH) authToken: String,
    ): Call<VatPercentageResponse>

    @GET(Keys.Api.DELETE_EXPENSE_TYPES + "/{id}")
    fun deleteExpenseTypes(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<BaseResponse>

    @GET(Keys.Api.GET_DROP_DOWN_STATUS)
    fun getStatusForDropDown(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<StatusDropDownResponse>>

    @GET(Keys.Api.GET_DROP_DOWN_COST_CENTER)
    fun getCostCenterForDropDown(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<CostCenterDropDownResponse>>

    @PUT(Keys.Api.PUT_EXPENSE_TYPES + "/{id}")
    fun putExpenseTypes(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>

    @POST(Keys.Api.POST_EXPENSE_TYPES)
    fun postExpenseTypes(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>

    /*Employment Types------------------------------*/
    @GET(Keys.Api.GET_EMPLOYMENT_TYPES)
    fun getEmploymentTypes(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<EmpTypeModel>>

    @GET(Keys.Api.GET_EMPLOYMENT_TYPE)
    fun getEmploymentType(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<EmpTypeModel>>

    @POST(Keys.Api.POST_EMPLOYMENT_TYPES)
    fun postEmploymentTypes(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>

    @PUT(Keys.Api.PUT_EMPLOYMENT_TYPES + "/{id}")
    fun putEmploymentTypes(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>

    @DELETE(Keys.Api.DELETE_EMPLOYMENT_TYPES + "/{id}")
    fun deleteEmploymentTypes(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<BaseResponse>
    /*Employment Type ends -------------------------*/


    /*Employees ------------------------------*/
    @GET(Keys.Api.GET_EMPLOYEES_FOR_DROP_DOWN)
    fun getEmployeesForDropDown(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<ProjectManagerResponse>>

    @GET(Keys.Api.GET_EMPLOYEES)
    fun getEmployees(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<EmployeesResponse>>

    @POST(Keys.Api.POST_EMPLOYEE)
    fun postEmployee(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>

    @PUT(Keys.Api.PUT_EMPLOYEE + "/{id}")
    fun putEmployee(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>

    @DELETE(Keys.Api.DELETE_EMPLOYEE + "/{id}")
    fun deleteEmployee(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<BaseResponse>
    /*Employees ends -------------------------*/


    /*Projects ------------------------------*/
    @GET(Keys.Api.GET_PROJECTS_FOR_DROP_DOWN)
    fun getProjectsForDropDown(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<ProjectsResponse>>

    @GET(Keys.Api.GET_EMPLOYEE_ASSIGNED_PROJECTS + "/{id}")
    fun getProjectsAssignedForEmployee(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ArrayList<AssignedProjectsResponse>>

    @GET(Keys.Api.GET_PROJECTS)
    fun getProjects(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<ProjectsResponse>>

    @GET(Keys.Api.GET_PROJECT + "/{id}")
    fun getProject(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ProjectsResponse>

    @POST(Keys.Api.POST_PROJECT)
    fun postProject(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>

    @PUT(Keys.Api.PUT_PROJECT + "/{id}")
    fun putProject(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>

    @DELETE(Keys.Api.DELETE_PROJECT + "/{id}")
    fun deleteProject(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<BaseResponse>
    /*Projects ends -------------------------*/

    /*Sub Projects ------------------------------*/
    @GET(Keys.Api.GET_SUB_PROJECTS_FOR_DROP_DOWN)
    fun getSubProjectsForDropDown(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<SubProjectsResponse>>

    @GET(Keys.Api.GET_SUB_PROJECTS_FOR_PROJECTS + "/{id}")
    fun getSubProjectsForProjects(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ArrayList<SubProjectsResponse>>

    @GET(Keys.Api.GET_SUB_PROJECTS)
    fun getSubProjects(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<SubProjectsResponse>>

    @GET(Keys.Api.GET_SUB_PROJECT)
    fun getSubProject(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<SubProjectsResponse>>

    @POST(Keys.Api.POST_SUB_PROJECT)
    fun postSubProject(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>

    @PUT(Keys.Api.PUT_SUB_PROJECT + "/{id}")
    fun putSubProject(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>

    @DELETE(Keys.Api.DELETE_SUB_PROJECT + "/{id}")
    fun deleteSubProject(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<BaseResponse>
    /*Sub Projects ends -------------------------*/

    /*Tasks ------------------------------*/
    @GET(Keys.Api.GET_WORK_TASKS_FOR_DROP_DOWN)
    fun getWorkTasksForDropDown(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<TasksResponse>>

    @GET(Keys.Api.GET_WORK_TASKS_FOR_SUB_PROJECTS + "/{id}")
    fun getWorkTasksForSubProject(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ArrayList<TasksResponse>>

    @GET(Keys.Api.GET_WORK_TASKS)
    fun getWorkTasks(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<TasksResponse>>

    @GET(Keys.Api.GET_WORK_TASK)
    fun getWorkTask(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<TasksResponse>>

    @POST(Keys.Api.POST_WORK_TASK)
    fun postWorkTask(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>

    @PUT(Keys.Api.PUT_WORK_TASK + "/{id}")
    fun putWorkTask(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>

    @DELETE(Keys.Api.DELETE_WORK_TASK + "/{id}")
    fun deleteWorkTask(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<BaseResponse>
    /*Tasks ends -------------------------*/

    /*Users-------------------------*/
    @GET(Keys.Api.GET_USERS)
    fun getUsers(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<UsersResponse>>

    @GET(Keys.Api.GET_USER)
    fun getUser(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<UsersResponse>>

//    @POST(Keys.Api.POST_USER)
//    fun postJobRoles(
//        @Header(Keys.UserData.AUTH) authToken: String,
//        @Body loginRequest: JsonObject
//    ): Call<Response<ApiResponse<BaseResponse>>>

    @PUT(Keys.Api.PUT_USER + "/{id}")
    fun putUser(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>

    @DELETE(Keys.Api.DELETE_USER + "/{id}")
    fun deleteUser(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: String
    ): Call<BaseResponse>
    /*Users ends -------------------------*/

    /*User Roles ----------------------------- */
    @GET(Keys.Api.GET_USER_ROLES)
    fun getUsersRoles(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<UserRolesResponse>>

    @DELETE(Keys.Api.DELETE_USER_ROLES + "/{id}")
    fun deleteUserRoles(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: String
    ): Call<BaseResponse>

    @PUT(Keys.Api.PUT_USER_ROLES + "/{id}")
    fun putUserRoles(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int,
        @Body loginRequest: JsonObject
    ): Call<Response<ApiResponse<BaseResponse>>>

    @POST(Keys.Api.POST_USER_ROLES)
    fun postUserRoles(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body loginRequest: JsonObject
    ): Call<Response<ApiResponse<BaseResponse>>>

    /*User Roles ends ------------------------------ */

    /*User Roles ----------------------------- */
    @GET(Keys.Api.GET_JOB_ROLES)
    fun getJobRoles(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<JobRolesResponse>>

    @DELETE(Keys.Api.DELETE_JOB_ROLES + "/{id}")
    fun deleteJobRoles(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<BaseResponse>

    @PUT(Keys.Api.PUT_JOB_ROLES + "/{id}")
    fun putJobRoles(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int,
        @Body loginRequest: JsonObject
    ): Call<Response<ApiResponse<BaseResponse>>>

    @POST(Keys.Api.POST_JOB_ROLES)
    fun postJobRoles(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body loginRequest: JsonObject
    ): Call<Response<ApiResponse<BaseResponse>>>
    /*User Roles ends ------------------------------ */

    /*approval group-------------------------*/

    @GET(Keys.Api.GET_APPROVAL_GROUPS)
    fun getApprovalGroups(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<ApprovalBaseResponse>>

    @GET(Keys.Api.GET_APPROVAL_GROUP + "/{id}")
    fun getApprovalGroup(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ArrayList<ApprovalBaseResponse>>

    @DELETE(Keys.Api.DELETE_APPROVAL_GROUP + "/{id}")
    fun deleteApprovalGroup(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<BaseResponse>

    @PUT(Keys.Api.PUT_APPROVAL_GROUP + "/{id}")
    fun putApprovalGroup(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>

    @POST(Keys.Api.POST_APPROVAL_GROUP)
    fun postApprovalGroup(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>
    /*approval group-------------------------*/

    /*approval level-------------------------*/
    @GET(Keys.Api.GET_APPROVAL_LEVELS)
    fun getApprovalLevels(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<ApprovalBaseResponse>>

    @GET(Keys.Api.GET_APPROVAL_LEVEL + "/{id}")
    fun getApprovalLevel(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ArrayList<ApprovalBaseResponse>>

    @DELETE(Keys.Api.DELETE_APPROVAL_LEVEL + "/{id}")
    fun deleteApprovalLevel(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<BaseResponse>

    @PUT(Keys.Api.PUT_APPROVAL_LEVEL + "/{id}")
    fun putApprovalLevel(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>

    @POST(Keys.Api.POST_APPROVAL_LEVEL)
    fun postApprovalLevel(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>
    /*approval level-------------------------*/

    /*approval role map-------------------------*/
    @GET(Keys.Api.GET_APPROVAL_ROLE_MAPS)
    fun getApprovalRoleMaps(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<ApprovalBaseResponse>>

    @GET(Keys.Api.GET_APPROVAL_ROLE_MAP + "/{id}")
    fun getApprovalRoleMap(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ArrayList<ApprovalBaseResponse>>

    @DELETE(Keys.Api.DELETE_APPROVAL_ROLE_MAP + "/{id}")
    fun deleteApprovalRoleMap(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<BaseResponse>

    @PUT(Keys.Api.PUT_APPROVAL_ROLE_MAP + "/{id}")
    fun putApprovalRoleMap(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>

    @POST(Keys.Api.POST_APPROVAL_ROLE_MAP)
    fun postApprovalRoleMap(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>
    /*approval role map-------------------------*/

    /*approval status types-------------------------*/
    @GET(Keys.Api.GET_APPROVAL_STATUS_TYPES)
    fun getApprovalStatusTypesOnly(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<InboxFragment.ApprovalStatusTypes>>

    @GET(Keys.Api.GET_APPROVAL_STATUS_TYPES)
    fun getApprovalStatusTypes(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<ApprovalBaseResponse>>

    @GET(Keys.Api.GET_APPROVAL_STATUS_TYPE + "/{id}")
    fun getApprovalStatusType(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ArrayList<ApprovalBaseResponse>>

    @DELETE(Keys.Api.DELETE_APPROVAL_STATUS_TYPE + "/{id}")
    fun deleteApprovalStatusType(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<BaseResponse>

    @PUT(Keys.Api.PUT_APPROVAL_STATUS_TYPE + "/{id}")
    fun putApprovalStatusType(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>

    @POST(Keys.Api.POST_APPROVAL_STATUS_TYPE)
    fun postApprovalStatusType(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>
    /*approval status types-------------------------*/

    /*currency types-------------------------*/
    @GET(Keys.Api.GET_CURRENCY_LIST)
    fun getCurrencyList(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<CurrencyResponse>>

    @GET(Keys.Api.GET_CURRENCY + "/{id}")
    fun getCurrency(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ArrayList<CurrencyResponse>>

    @DELETE(Keys.Api.DELETE_CURRENCY + "/{id}")
    fun deleteCurrency(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<BaseResponse>

    @PUT(Keys.Api.PUT_CURRENCY + "/{id}")
    fun putCurrency(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>

    @POST(Keys.Api.POST_CURRENCY)
    fun postCurrency(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>
    /*currency types-------------------------*/


    /*Common drop down api*/
    @GET(Keys.Api.VENDORS_FOR_DROP_DOWN)
    fun getVendorsForDropDown(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<VendorDropDownItem>>

    @GET(Keys.Api.GET_BUSINESS_TYPE_FOR_DROP_DOWN)
    fun getBusinessTypesForDropDown(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<BusinessTypesForDropDownResponseItem>>

    @POST(Keys.Api.GET_BUSINESS_UNITS_FOR_DROP_DOWN)
    fun getBusinessUnitsForDropDown(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body requestJson: JsonObject
    ): Call<ArrayList<BusinessUnitsListResponseItem>>

    @GET(Keys.Api.GET_BUSINESS_UNIT_DETAILS + "/{id}")
    fun getBusinessUnitsDetails(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<BusinessUnitDetails>

    @GET(Keys.Api.GET_DROP_DOWN_CURRENCY_TYPES)
    fun getCurrencyTypesForDropDown(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<CurrencyDropDownResponse>>

    @GET(Keys.Api.GET_DROP_DOWN_APPROVAL_GROUPS)
    fun getApprovalGroupsForDropDown(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<ApprovalGroupDropDownResponse>>

    @GET(Keys.Api.GET_DROP_DOWN_JOB_ROLES)
    fun getJobRolesForDropDown(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<JobRolesDropDownResponse>>

    @GET(Keys.Api.GET_DROP_DOWN_DEPARTMENT)
    fun getDepartmentsForDropDown(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<DepartmentDropDownResponse>>

    @GET(Keys.Api.GET_DROP_DOWN_EMPLOYMENT_TYPES)
    fun getEmploymentTypesForDropDown(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<EmpTypesDropDownResponse>>

    @GET(Keys.Api.GET_CURRENCY_LIST_FOR_DROP_DOWN)
    fun getCurrencyListForDropdown(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<CurrencyResponse>>
    /*Common drop down api ends*/

    /*project management*/
    @POST(Keys.Api.POST_PROJECT_MANAGEMENT)
    fun assignProject(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>
    /*project management ends*/

    /*assignRoles management*/
    @POST(Keys.Api.ASSIGN_ROLES)
    fun assignRoles(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body requestJson: JsonObject
    ): Call<BaseResponse>
    /*assignRoles ends*/

    /*Cash advance ----------------*/
    @GET(Keys.Api.GET_PETTY_CASH_REQUESTS)
    fun getAllPettyCashRequests(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<PettyCashResponse>>

    @GET(Keys.Api.GET_PETTY_CASH_REQUEST + "/{id}")
    fun getPettyCashRequest(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<PettyCashResponse>

    @GET(Keys.Api.GET_PETTY_CASH_REQUEST_RAISED_FOR_EMPLOYEE + "/{id}")
    fun getPettyCashRequestRaisedForEmployee(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ArrayList<PettyCashResponse>>

    @GET(Keys.Api.GET_PETTY_CASH_REQUEST_COUNT_RAISED_BY_EMPLOYEE + "/{id}")
    fun countPettyCashRequestRaisedByEmployee(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<PettyCashCountResponse>

    @GET(Keys.Api.GET_EXPENSE_REIMBURSE_COUNT_RAISED_BY_EMPLOYEE + "/{id}")
    fun countExpenseRequestRaisedByEmployee(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ExpenseReimburseCountResponse>

    @GET(Keys.Api.GET_TRAVEL_EXPENSE_COUNT_RAISED_BY_EMPLOYEE + "/{id}")
    fun countTravelRequestRaisedByEmployee(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ExpenseReimburseCountResponse>

    @GET(Keys.Api.GET_PETTY_CASH_REQUEST_PENDING_FOR_ALL)
    fun getAllPendingPettyCashRequest(@Header(Keys.UserData.AUTH) authToken: String): Call<BaseResponse>

    @PUT(Keys.Api.PUT_PETTY_CASH_REQUEST + "/{id}")
    fun putPettyCashRequest(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int,
        @Body jsonObject: JsonObject
    ): Call<BaseResponse>

    @POST(Keys.Api.POST_PETTY_CASH_REQUEST)
    fun postPettyCashRequest(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body jsonObject: JsonObject
    ): Call<BaseResponse>

    @DELETE(Keys.Api.DELETE_PETTY_CASH_REQUEST + "/{id}")
    fun deletePettyCashRequest(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<BaseResponse>
    /*Cash advance ends----------------------------------*/

    /*Inbox starts*/
    @GET(Keys.Api.INBOX_CASH_ADVANCE + "/{id}")
    fun getCashAdvanceList(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ArrayList<InboxCashAdvanceResponse>>

    @GET(Keys.Api.INBOX_CASH_ADVANCE_COUNT + "/{id}")
    fun getCashAdvanceCount(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<CashAdvanceCountResponseData>

    @GET(Keys.Api.INBOX_EXPENSE_REIMBURSE + "/{id}")
    fun getExpenseList(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ArrayList<InboxExpenseReimburseResponse>>

    @GET(Keys.Api.INBOX_TRAVEL_REQUEST + "/{id}")
    fun getTravelRequestList(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ArrayList<InboxTravelRequestResponse>>

    @PUT(Keys.Api.APPROVE_REJECT_CASH_ADVANCE)
    fun approveRejectCashAdvances(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body jsonArray: JsonArray
    ): Call<BaseResponse>

    @PUT(Keys.Api.APPROVE_REJECT_TRAVEL_REQUEST)
    fun approveRejectTravelExpenses(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body jsonArray: JsonArray
    ): Call<BaseResponse>

    @PUT(Keys.Api.APPROVE_REJECT_EXPENSE_REIMBURSE)
    fun approveRejectExpenseReimburse(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body jsonArray: JsonArray
    ): Call<BaseResponse>
    /*Inbox ends*/

    @GET(Keys.Api.APPROVAL_FLOW_FOR_REQUEST + "/{id}")
    fun approvalFlowForRequest(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ArrayList<TimeLineItem>>


    @GET(Keys.Api.CURRENT_PETTY_CASH_BALANCE + "/{id}")
    fun getPettyCashBalance(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<PettyCashBalanceResponse>


    @GET(Keys.Api.CURRENT_PETTY_CASH_BALANCE_ADVANCED + "/{id}")
    fun getPettyCashBalanceVsAdvanced(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<PettyCashBalanceVsAdvancedResponse>

    @GET(Keys.Api.GET_EXPENSE_REIMBURSE_RAISED_FOR_EMPLOYEE + "/{id}")
    fun getExpenseReimburseRaisedForEmployee(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ArrayList<ExpenseRaisedForEmployeeResponse>>

    @GET(Keys.Api.GET_EXPENSE_REIMBURSE_REQUEST + "/{id}")
    fun getExpenseReimburseRequest(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<InboxExpenseReimburseResponse>

    @DELETE(Keys.Api.DELETE_EXPENSE_REIMBURSE_REQUEST + "/{id}")
    fun deleteExpenseReimburseRequest(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<BaseResponse>

    @GET(Keys.Api.APPROVAL_FLOW_FOR_EXPENSE_REIMBURSE_REQUEST + "/{id}")
    fun approvalFlowForExpenseReimburseRequest(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ArrayList<TimeLineItem>>

    @Multipart
    @POST(Keys.Api.POST_DOCUMENTS)
    fun postDocuments(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Part file: ArrayList<MultipartBody.Part>
    ): Call<ArrayList<PostDocumentsResponse>>

    @POST(Keys.Api.POST_EXPENSE_REIMBURSE)
    fun postExpenseReimburse(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body jsonObject: ExpenseReimburseRequestDto
    ): Call<BaseResponse>

    /*Travel request ----------------*/
    @GET(Keys.Api.GET_TRAVEL_REQUESTS)
    fun getAllTravelRequests(@Header(Keys.UserData.AUTH) authToken: String): Call<ArrayList<TravelResponse>>

    @GET(Keys.Api.GET_TRAVEL_REQUEST_FOR_EMPLOYEE + "/{id}")
    fun getTravelRequest(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ArrayList<TravelResponse>>

    @GET(Keys.Api.GET_TRAVEL_REQUEST_DETAIL + "/{id}")
    fun getTravelRequestDetail(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<TravelResponse>

    @PUT(Keys.Api.PUT_TRAVEL_REQUEST + "/{id}")
    fun putTravelRequest(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int,
        @Body jsonObject: ManageTravelRequestActivity.TravelRequestDto
    ): Call<BaseResponse>

    @POST(Keys.Api.POST_TRAVEL_REQUEST)
    fun postTravelRequest(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Body jsonObject: TravelRequestDto
    ): Call<BaseResponse>

    @DELETE(Keys.Api.DELETE_TRAVEL_REQUEST + "/{id}")
    fun deleteTravelRequest(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<BaseResponse>
    /*Travel request ends----------------------------------*/

    @GET(Keys.Api.GET_EXPENSE_REIMBURSE_SUB_CLAIMS + "/{id}")
    fun getExpenseSubClaims(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ArrayList<ExpenseReimburseListingResponse>>

    @GET(Keys.Api.GET_EXPENSE_REIMBURSE_SUB_CLAIMS_DOCUMENTS + "/{id}")
    fun getExpenseSubClaimsDocs(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ArrayList<String>>

    @GET(Keys.Api.GET_EMP_CUR_BAL_AND_CASH_IN_HAND + "/{id}")
    fun getCurrentBalanceAndCashInHand(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<MaxLimitBalanceAndCashInHand>

    @GET(Keys.Api.GET_EMPLOYEE + "/{id}")
    fun getEmployee(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<EmployeesResponse>

    @GET(Keys.Api.TRAVEL_APPROVAL_FLOW_FOR_REQUEST + "/{id}")
    fun approvalFlowForTravelRequest(
        @Header(Keys.UserData.AUTH) authToken: String,
        @Path("id") id: Int
    ): Call<ArrayList<TimeLineItem>>
}
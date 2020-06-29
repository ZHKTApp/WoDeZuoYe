package com.zwyl.myhomework.service;

import com.zwyl.myhomework.dialog.bean.BeanAllYear;
import com.zwyl.myhomework.dialog.bean.BeanNowYear;
import com.zwyl.myhomework.dialog.bean.DownloadWorkBean;
import com.zwyl.myhomework.dialog.bean.UpdateBean;
import com.zwyl.myhomework.http.HttpResult;
import com.zwyl.myhomework.main.BeanHomeGrid;
import com.zwyl.myhomework.main.BeanTextBookGrid;
import com.zwyl.myhomework.main.detaile.BeanCatelog;
import com.zwyl.myhomework.main.detaile.BeanDetaile;
import com.zwyl.myhomework.main.detaile.BeanNote;
import com.zwyl.myhomework.main.details.ResultInfoBean;
import com.zwyl.myhomework.main.subject.BeanSubject;
import com.zwyl.myhomework.main.subject.BeanSubjectFile;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserService {
    @FormUrlEncoded
    @POST("user/login")
    Observable<HttpResult<String>> login(@Field("password") String password, @Field("username") String username);

    /**
     * 1.查询当前学年接口：
     * 调用接口：/ys-manager/guidebook/currentYearByStudentId
     * 接口说明：首页显示该学生的当前学年
     * 请求方式：post
     * <p>
     * 参数名称	参数命名	说明
     * 用户token	用户登录时获得的token	用户登录后，后端返回到app的token凭证，是个uuid
     * <p>
     * <p>
     * 返回数据列表：
     * 名称	字段命名	说明
     * 学年id	academicYearId
     * 学年名称	academicYear
     */
    @FormUrlEncoded
    @POST("guidebook/currentYearByStudentId")
    Observable<HttpResult<BeanNowYear>> currentYearByStudentId(@Field("nullStr") String nullStr);

    /**
     * 2.查询该学生所有学年接口：
     * 调用接口：/ys-manager/guidebook/allYearsByStudentId
     * 接口说明：点击该学生的当前学年查询所有历史学年接口
     * 请求方式：post
     * <p>
     * 参数名称	参数命名	说明
     * 用户token	用户登录时获得的token	用户登录后，后端返回到app的token凭证，是个uuid
     * <p>
     * <p>
     * 返回数据列表：
     * 名称	字段命名	说明
     * 学年id	academicYearId
     * 学年名称	academicYear
     **/
    @FormUrlEncoded
    @POST("guidebook/allYearsByStudentId")
    Observable<HttpResult<List<BeanAllYear>>> allYearsByStudentId(@Field("nullStr") String nullStr);

    /**
     * 3.根据学年查该学生所在年级的所有科目
     * 调用接口：/guidebook/selectGradeSubejectList
     * 接口说明：选择学年显示学年下的所有科目
     * 刚进入页面默认返回当前学年的科目，选择学年后返回该学年下的科目
     * 请求方式：post
     * <p>
     * 请求参数：
     * 名称	字段命名	说明
     * 学年id	academicYearId
     * 用户token	Token
     * 当前页	pageNo
     * 每页显示条数	PageSize	默认一页10条数据
     * <p>
     * <p>
     * 返回数据列表：
     * 名称	字段命名	说明
     * 科目id	schoolSubjectId
     * 科目名称	schoolSubjectName
     **/
    @FormUrlEncoded
    @POST("guidebook/selectGradeSubejectList")
    Observable<HttpResult<List<BeanHomeGrid>>> selectGradeSubejectList(@Field("academicYearId") String password);

    /**
     * 4.根据学年和科目查该学生课本详情
     * 调用接口：/guidebook/selectTextBook
     * 接口说明：显示科目下的课本接口
     * 请求方式：post
     * <p>
     * 请求参数：
     * 名称	字段命名	说明
     * 学年id	academicYearId
     * 用户token	Token
     * 科目id	schoolSubjectId
     **/
    @FormUrlEncoded
    @POST("guidebook/selectTextBook")
    Observable<HttpResult<List<BeanTextBookGrid>>> selectTextBook(@Field("academicYearId") String academicYearId, @Field("schoolSubjectId") String schoolSubjectId);

    /**
     * 5.课本树结构接口
     * 调用接口：/guidebook/selectTextBookChapter
     * 接口说明：显示课本章节结构
     * 请求方式：post
     * <p>
     * 请求参数：
     * 名称	字段命名	说明
     * 课本id	textBookId
     * 课本章节父id	textBookChapterParentId	用户点击哪个章节，就把该章节id当成章节父id传给后台，查出它下面的子章节
     **/
    @FormUrlEncoded
    @POST("guidebook/selectTextBookChapter")
//    Observable<HttpResult<List<BeanCatelog>>> selectTextBookChapter(@Field("textBookId") String textBookId);
    Observable<HttpResult<List<ResultInfoBean>>> selectTextBookChapter(@Field("textBookId") String textBookId);

    /**
     * 1.查课本下所有网络作业接口：
     * 调用接口：/homework/selectHomeworkByTextBookId
     * 请求方式：post
     * <p>
     * 请求参数：
     * 名称	字段命名	说明
     * 课本id	textBookId
     * 学生	Token
     * 排序方式	orderBy
     * 未完成/已完成状态	completedStatus	未完成参数值传：uncompleted
     * 已完成参数值传：completed
     **/
    @FormUrlEncoded
    @POST("homework/selectHomeworkByTextBookId")
    Observable<HttpResult<List<BeanDetaile>>> selectHomeworkByTextBookId(@Field("textBookId") String textBookId, @Field("orderBy") String orderBy, @Field("completedStatus") String completedStatus);

    /**
     * 1.查课本下所有网络作业接口：
     * 调用接口：/homework/selectHomeworkByChapterId
     * 请求方式：post
     * <p>
     * 请求参数：
     * 名称	字段命名	说明
     * 课本id	textBookId
     * 学生	Token
     * 排序方式	orderBy
     * 未完成/已完成状态	completedStatus	未完成参数值传：uncompleted
     * 已完成参数值传：completed
     **/
    @FormUrlEncoded
    @POST("homework/selectHomeworkByChapterId")
//    @POST("homework/selectHomeworkByTextBookId")
    Observable<HttpResult<List<BeanDetaile>>> selectHomeworkByChapterId(@Field("textBookChapterId") String textBookId, @Field("orderBy") String orderBy, @Field("completedStatus") String completedStatus);

    /**
     * 7.点击章节查章节下的导学本
     * 调用接口：/guidebook/selectGuideBookByChapterId
     * 接口说明：
     * 请求方式：post
     * <p>
     * 请求参数：
     * 名称	字段命名	说明
     * 章节id	textBookChapterId
     * 课本章节父id	token
     * 排序参数	orderBy	刚进页面默认按时间升序排列，之后升序传asc,降序传desc
     * <p>
     * 返回数据：返回list。每个实体类只有三个参数有用。
     * 名称	字段命名	说明
     * 导学本id	guideSoureId
     * 导学本名称	sourceName
     * 创建人	teacherName
     * 创建时间	createTime
     * 导学本url	fileUri
     * 是否有测试题	isExerciseExit	True：有，false：没有
     * 是否下载过	isDownload	True：有，false：没有
     * <p>
     * 正确返回格式同上：list数据
     **/
    @FormUrlEncoded
    @POST("workSupport/selectWorkSupportByChapterId")
    Observable<HttpResult<List<BeanDetaile>>> selectWorkSupportByChapterId(@Field("textBookChapterId") String textBookId, @Field("orderBy") String orderBy);

    /**
     * 新增导学笔记接口：
     * 调用接口：/guidebook/insertGuideNoteBook
     * 接口说明：导学笔记新增
     * 请求方式：post
     * <p>
     * 请求参数：
     * 名称	字段命名	说明
     * 导学本id	guideSourceId
     * 学生	Token
     * 笔记内容	noteContent
     **/
    @FormUrlEncoded
    @POST("guidebook/insertGuideNoteBook")
    Observable<HttpResult<String>> insertGuideNoteBook(@Field("guideSourceId") String guideSourceId, @Field("noteContent") String noteContent);

    /**
     * 查导学笔记接口：
     * 调用接口：/guidebook/selectSourceNoteBook
     * 接口说明：查所有导学笔记
     * 请求方式：post
     * <p>
     * 请求参数：
     * 名称	字段命名	说明
     * 导学本id	guideSourceId
     * 学生	Token
     * <p>
     * 返回数据：返回list。返回多个参数，只用下面三个
     * 名称	字段命名	说明
     * 笔记id	noteId
     * 笔记内容	noteContent
     * 创建时间	createTime
     **/
    @FormUrlEncoded
    @POST("guidebook/selectSourceNoteBook")
    Observable<HttpResult<List<BeanNote>>> selectSourceNoteBook(@Field("guideSourceId") String guideSourceId);

    /**
     * 8.学生下载导学本，给后台传参，后台记录下载状态
     * 调用接口：/guidebook/downloadGuideBook
     * 接口说明：
     * 请求方式：post
     * <p>
     * 请求参数：
     * 名称	字段命名	说明
     * 导学本id	guideSoureId
     * 课本章节父id	token
     **/
    @FormUrlEncoded
    @POST("guidebook/downloadGuideBook")
    Observable<HttpResult<String>> downloadGuideBook(@Field("guideSourceId") String guideSourceId);

    /**
     * 查导学测试题接口：
     * 调用接口：/guidebook/selectGuideExercises
     * 接口说明：查导学本下面的测试题
     * 请求方式：post
     * <p>
     * 请求参数：
     * 名称	字段命名	说明
     * 导学本id	guideSourceId
     * cmStudentId
     **/
    @FormUrlEncoded
    @POST("workSupport/selectWorkSupportInfo")
    Observable<HttpResult<List<BeanSubject>>> selectWorkSupportInfo(@Field("workSupportId") String workSupportId);

    /**
     * 学生提交作业接口：
     * 调用接口：/guidebook/submitWork
     * 接口说明：学生提交答案，题库作业，附件作业
     * 请求方式：post
     * <p>
     * 请求参数：
     * 名称	字段命名	说明
     * 学生	Token
     * 作业id，存作业id和导学测试的导学本id	workId
     * 作业记录id	workPushLogId
     * 作业类型	workType
     * 题目详情	info
     * 题目id	exerciseId
     * 答案	studentAnswer
     **/
    @Multipart
    @POST("homework/submitWork")
    Observable<HttpResult<String>> submitWork(@Part List<MultipartBody.Part> file);

    /**
     * 加入错题集接口。。网络作业，作业辅导等app加入错题集都用该接口
     * 调用接口：/exercisesCollection/addMistakesCollection
     * 接口说明：加入错题集通用接口，别的作业辅导，我的作业公用接口
     * 请求方式：post
     **/
    @FormUrlEncoded
    @POST("exercisesCollection/addMistakesCollection")
    Observable<HttpResult<String>> addMistakesCollection(@FieldMap Map<String, String> map);

    /**
     * 移出错题集接口.
     * 调用接口：/exercisesCollection/deleteMistakesCollection
     * 接口说明：作业辅导，我的作业公用接口
     * 请求方式：post
     **/
    @FormUrlEncoded
    @POST("exercisesCollection/deleteMistakesCollection")
    Observable<HttpResult<String>> deleteMistakesCollection(@Field("workId") String workId, @Field("exerciseId") String exerciseId);

    /**
     * 3.查作业详情：
     * 调用接口：/homework/selectHomeworkInfo
     * 请求方式：post
     * <p>
     * 请求参数：
     * 名称	字段命名	说明
     * 学生id	cmStudentId
     * 作业id	onlineWorkId
     * 作业记录id	workPushLogId
     * 作业类型	onlineWorkType
     **/
    @FormUrlEncoded
    @POST("homework/selectHomeworkInfo")
    Observable<HttpResult<BeanSubjectFile>> selectHomeworkInfoFile(@Field("onlineWorkId") String onlineWorkId, @Field("workPushLogId") String workPushLogId, @Field("onlineWorkType") String onlineWorkType);

    /**
     * 3.查作业详情：
     * 调用接口：/homework/selectHomeworkInfo
     * 请求方式：post
     * <p>
     * 请求参数：
     * 名称	字段命名	说明
     * 学生id	cmStudentId
     * 作业id	onlineWorkId
     * 作业记录id	workPushLogId
     * 作业类型	onlineWorkType
     **/
    @FormUrlEncoded
    @POST("homework/selectHomeworkInfo")
    Observable<HttpResult<BeanSubject>> selectHomeworkInfo(@Field("onlineWorkId") String onlineWorkId, @Field("workPushLogId") String workPushLogId, @Field("onlineWorkType") String onlineWorkType);

    /**
     * 求讲解接口：
     * 调用接口：/homework/requestExplain
     * 接口说明：
     * 请求方式：post
     * <p>
     * 请求参数：
     * 名称	字段命名	说明
     * 网络作业id	onlineWorkId
     * 学生	Token
     * 作业记录id	workPushLogId
     * 习题id	exerciseId
     **/
    @FormUrlEncoded
    @POST("homework/requestExplain")
    Observable<HttpResult<String>> requestExplain(@Field("onlineWorkId") String onlineWorkId, @Field("workPushLogId") String workPushLogId, @Field("exerciseId") String exerciseId);

    /**
     * 简易作业
     */
    @Multipart
    @POST("homework/submitSimpleHomework")
    Observable<HttpResult<String>> submitSimpleHomework(@Part List<MultipartBody.Part> file);

    /**
     * 接口名：/app/guidebook/selectAppUpdate
     * <p>
     * 参数：cmStudentId,appId(应用id)
     * <p>
     * 返回参数：
     * appVersionId:版本号id
     * appVersionName：版本名称
     * fileUrl：文件url
     */
    @FormUrlEncoded
    @POST("guidebook/selectAppUpdate")
    Observable<HttpResult<UpdateBean>> selectAppUpdate(@Field("appId") String appId);

    /**
     * 下载网络作业
     * 调用接口：/app/homework/downLoadOnlineWork
     * 网络作业id	onlineWorkId	网络作业id
     * 网络作业类型	onlineWorkType	作业类型
     * downLoadUrl":"",//下载地址
     */
    @FormUrlEncoded
    @POST("homework/downLoadOnlineWork")
    Observable<HttpResult<DownloadWorkBean>> downLoadOnlineWork(@Field("onlineWorkId") String onlineWorkId, @Field("onlineWorkType") int onlineWorkType);

    /**
     * 调用接口：/app/homework/addlog
     * 接口说明：公共接口------资源观看人数接口
     * 请求方式：post
     * <p>
     * 请求参数：
     * 参数名称	参数命名	说明	举例
     * 学生id	cmStudentId	学生id	asdfasdaf
     * 资源id	resourceId	资源id	asdfasda
     * 资源类型code	type	导学本：801,网络作业：501 ，作业辅导：901，微课：90102，网络课程：1101
     * 教师id	cmTeacherId	教师id	asdfasda
     * 课本id	textBookId	课本id	asdfasda
     *
     * @return {
     * "resultMsg": "OK",
     * "resultCode": 200,
     * "len": 0,
     * "resultInfo": null,
     * "token": null,
     * "success": true,
     * "existsData": false,
     * "dataLength": 0
     * }
     */
    @FormUrlEncoded
    @POST("homework/addlog")
    Observable<HttpResult<String>> addlog(@Field("resourceId") String resourceId, @Field("type") int codeType, @Field("cmTeacherId") String cmTeacherId, @Field("textBookId") String textBookId);

}
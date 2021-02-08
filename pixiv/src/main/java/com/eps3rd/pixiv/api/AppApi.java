package com.eps3rd.pixiv.api;

import com.eps3rd.pixiv.model.ListArticle;
import com.eps3rd.pixiv.model.ListBookmarkTag;
import com.eps3rd.pixiv.model.ListComment;
import com.eps3rd.pixiv.model.ListIllust;
import com.eps3rd.pixiv.model.ListLive;
import com.eps3rd.pixiv.model.ListMangaOfSeries;
import com.eps3rd.pixiv.model.ListMangaSeries;
import com.eps3rd.pixiv.model.ListNovel;
import com.eps3rd.pixiv.model.ListNovelOfSeries;
import com.eps3rd.pixiv.model.ListNovelSeries;
import com.eps3rd.pixiv.model.ListSimpleUser;
import com.eps3rd.pixiv.model.ListTag;
import com.eps3rd.pixiv.model.ListTrendingtag;
import com.eps3rd.pixiv.model.ListUser;
import com.eps3rd.pixiv.models.CommentHolder;
import com.eps3rd.pixiv.models.GifResponse;
import com.eps3rd.pixiv.models.IllustSearchResponse;
import com.eps3rd.pixiv.models.MutedHistory;
import com.eps3rd.pixiv.models.NovelDetail;
import com.eps3rd.pixiv.models.NovelSearchResponse;
import com.eps3rd.pixiv.models.NullResponse;
import com.eps3rd.pixiv.models.Preset;
import com.eps3rd.pixiv.models.UserDetailResponse;
import com.eps3rd.pixiv.models.UserState;

import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface AppApi {
    /**
     * 获取排行榜
     *
     * @param mode
     * @return
     */
    @GET("v1/illust/ranking?filter=for_android")
    Call<ListIllust> getRank(@Header("Authorization") String token,
                             @Query("mode") String mode,
                             @Query("date") String date);

    @GET("v1/novel/ranking?filter=for_android")
    Call<ListNovel> getRankNovel(@Header("Authorization") String token,
                                 @Query("mode") String mode,
                                 @Query("date") String date);

    /**
     * 推荐榜单
     *
     * @param token
     * @return
     */
    @GET("v1/illust/recommended?include_privacy_policy=true&filter=for_android&include_ranking_illusts=true")
    Call<ListIllust> getRecmdIllust(@Header("Authorization") String token);


    @GET("v1/manga/recommended?include_privacy_policy=true&filter=for_android&include_ranking_illusts=true")
    Call<ListIllust> getRecmdManga(@Header("Authorization") String token);

    @GET("v1/novel/recommended?include_privacy_policy=true&filter=for_android&include_ranking_novels=true")
    Call<ListNovel> getRecmdNovel(@Header("Authorization") String token);

    @GET("v1/novel/follow?restrict=public")
    Call<ListNovel> getBookedUserSubmitNovel(@Header("Authorization") String token);


    @GET("v1/trending-tags/{type}?filter=for_android&include_translated_tag_results=true")
    Call<ListTrendingtag> getHotTags(@Header("Authorization") String token,
                                           @Path("type") String type);


    /**
     * 原版app登录时候的背景墙
     *
     * @param token
     * @return
     */
    @GET("v1/walkthrough/illusts?filter=for_android")
    Call<ListIllust> getLoginBg(@Header("Authorization") String token);


    /**
     * /v1/search/illust?
     * filter=for_android&
     * include_translated_tag_results=true&
     * word=%E8%89%A6%E9%9A%8A%E3%81%93%E3%82%8C%E3%81%8F%E3%81%97%E3%82%87%E3%82%93&
     * sort=date_desc& 最新
     * sort=date_asc& 旧的在前面
     * search_target=exact_match_for_tags 标签完全匹配
     * search_target=partial_match_for_tags 标签部分匹配
     * search_target=title_and_caption 标题或简介
     */
    @GET("v1/search/illust?filter=for_android&include_translated_tag_results=true")
    Call<ListIllust> searchIllust(@Header("Authorization") String token,
                                        @Query("word") String word,
                                        @Query("sort") String sort,
                                        @Query("search_target") String search_target);

    @GET("v1/search/novel?filter=for_android&include_translated_tag_results=true")
    Call<ListNovel> searchNovel(@Header("Authorization") String token,
                                      @Query("word") String word,
                                      @Query("sort") String sort,
                                      @Query("search_target") String search_target);


    @GET("v2/illust/related?filter=for_android")
    Call<ListIllust> relatedIllust(@Header("Authorization") String token,
                                         @Query("illust_id") int illust_id);


    /**
     * 推荐用户
     *
     * @param token
     * @return
     */
    @GET("v1/user/recommended?filter=for_android")
    Call<ListUser> getRecmdUser(@Header("Authorization") String token);


    @GET("v1/user/bookmarks/illust")
    Call<ListIllust> getUserLikeIllust(@Header("Authorization") String token,
                                             @Query("user_id") int user_id,
                                             @Query("restrict") String restrict,
                                             @Query("tag") String tag);

    @GET("v1/user/bookmarks/illust")
    Call<ListIllust> getUserLikeIllust(@Header("Authorization") String token,
                                             @Query("user_id") int user_id,
                                             @Query("restrict") String restrict);

    @GET("v1/user/bookmarks/novel")
    Call<ListNovel> getUserLikeNovel(@Header("Authorization") String token,
                                           @Query("user_id") int user_id,
                                           @Query("restrict") String restrict);

    @GET("v1/user/illusts?filter=for_android")
    Call<ListIllust> getUserSubmitIllust(@Header("Authorization") String token,
                                               @Query("user_id") int user_id,
                                               @Query("type") String type);

    @GET("v1/user/novels")
    Call<ListNovel> getUserSubmitNovel(@Header("Authorization") String token,
                                             @Query("user_id") int user_id);


    @GET("v2/illust/follow")
    Call<ListIllust> getFollowUserIllust(@Header("Authorization") String token,
                                               @Query("restrict") String restrict);


    @GET("v1/spotlight/articles?filter=for_android")
    Call<ListArticle> getArticles(@Header("Authorization") String token,
                                        @Query("category") String category);


    ///v1/user/detail?filter=for_android&user_id=24218478
    @GET("v1/user/detail?filter=for_android")
    Call<UserDetailResponse> getUserDetail(@Header("Authorization") String token,
                                           @Query("user_id") int user_id);


    //  /v1/ugoira/metadata?illust_id=47297805
    @GET("v1/ugoira/metadata")
    Call<GifResponse> getGifPackage(@Header("Authorization") String token,
                                    @Query("illust_id") int illust_id);


    @FormUrlEncoded
    @POST("v1/user/follow/add")
    Call<NullResponse> postFollow(@Header("Authorization") String token,
                                  @Field("user_id") int user_id,
                                  @Field("restrict") String followType);

    @FormUrlEncoded
    @POST("v1/user/follow/delete")
    Call<NullResponse> postUnFollow(@Header("Authorization") String token,
                                          @Field("user_id") int user_id);


    /**
     * 获取userid 所关注的人
     *
     * @param token
     * @param user_id
     * @param restrict
     * @return
     */
    @GET("v1/user/following?filter=for_android")
    Call<ListUser> getFollowUser(@Header("Authorization") String token,
                                       @Query("user_id") int user_id,
                                       @Query("restrict") String restrict);


    //获取关注 这个userid 的人
    @GET("v1/user/follower?filter=for_android")
    Call<ListUser> getWhoFollowThisUser(@Header("Authorization") String token,
                                              @Query("user_id") int user_id);


    @GET("v1/illust/comments")
    Call<ListComment> getComment(@Header("Authorization") String token,
                                 @Query("illust_id") int illust_id);


    @GET
    Call<ListComment> getNextComment(@Header("Authorization") String token,
                                           @Url String nextUrl);


    @FormUrlEncoded
    @POST("v1/illust/comment/add")
    Call<CommentHolder> postComment(@Header("Authorization") String token,
                                    @Field("illust_id") int illust_id,
                                    @Field("comment") String comment);

    @FormUrlEncoded
    @POST("v1/illust/comment/add")
    Call<CommentHolder> postComment(@Header("Authorization") String token,
                                          @Field("illust_id") int illust_id,
                                          @Field("comment") String comment,
                                          @Field("parent_comment_id") int parent_comment_id);

    @FormUrlEncoded
    @POST("v2/illust/bookmark/add")
    Call<NullResponse> postLike(@Header("Authorization") String token,
                                      @Field("illust_id") int illust_id,
                                      @Field("restrict") String restrict);

    @FormUrlEncoded
    @POST("v2/novel/bookmark/add")
    Call<NullResponse> postLikeNovel(@Header("Authorization") String token,
                                           @Field("novel_id") int novel_id,
                                           @Field("restrict") String restrict);

    @FormUrlEncoded
    @POST("v2/illust/bookmark/add")
    Call<NullResponse> postLike(@Header("Authorization") String token,
                                      @Field("illust_id") int illust_id,
                                      @Field("restrict") String restrict,
                                      @Field("tags[]") String... tags);

    @FormUrlEncoded
    @POST("v1/illust/bookmark/delete")
    Call<NullResponse> postDislike(@Header("Authorization") String token,
                                         @Field("illust_id") int illust_id);

    @FormUrlEncoded
    @POST("v1/novel/bookmark/delete")
    Call<NullResponse> postDislikeNovel(@Header("Authorization") String token,
                                              @Field("novel_id") int novel_id);


    @GET("v1/illust/detail?filter=for_android")
    Call<IllustSearchResponse> getIllustByID(@Header("Authorization") String token,
                                             @Query("illust_id") int illust_id);


    @GET("v1/search/user?filter=for_android")
    Call<ListUser> searchUser(@Header("Authorization") String token,
                                    @Query("word") String word);


    @GET("v1/search/popular-preview/illust?filter=for_android&include_translated_tag_results=true&merge_plain_keyword_results=true&search_target=exact_match_for_tags")
    Call<ListIllust> popularPreview(@Header("Authorization") String token,
                                          @Query("word") String word);

    @GET("v1/search/popular-preview/novel?filter=for_android&include_translated_tag_results=true&merge_plain_keyword_results=true&search_target=exact_match_for_tags")
    Call<ListNovel> popularNovelPreview(@Header("Authorization") String token,
                                          @Query("word") String word);


    // v2/search/autocomplete?merge_plain_keyword_results=true&word=%E5%A5%B3%E4%BD%93 HTTP/1.1
    @GET("v2/search/autocomplete?merge_plain_keyword_results=true")
    Call<ListTrendingtag> searchCompleteWord(@Header("Authorization") String token,
                                                   @Query("word") String word);


    /**
     * 获取收藏的标签
     */
    //GET v1/user/bookmark-tags/illust?user_id=41531382&restrict=public HTTP/1.1
    @GET("v1/user/bookmark-tags/illust")
    Call<ListTag> getBookmarkTags(@Header("Authorization") String token,
                                  @Query("user_id") int user_id,
                                  @Query("restrict") String restrict);


    @GET
    Call<ListTag> getNextTags(@Header("Authorization") String token,
                                    @Url String nextUrl);


    // GET v2/illust/bookmark/detail?illust_id=72878894 HTTP/1.1


    @GET("v2/illust/bookmark/detail")
    Call<ListBookmarkTag> getIllustBookmarkTags(@Header("Authorization") String token,
                                                @Query("illust_id") int illust_id);


    /**
     * 获取已屏蔽的标签/用户
     * <p>
     * 这功能感觉做了没啥卵用，因为未开会员的用户只能屏蔽一个标签/用户，
     * <p>
     * 你屏蔽了一个用户，就不能再屏蔽标签，屏蔽了标签，就不能屏蔽用户，而且都只能屏蔽一个，擦
     *
     * @param token
     * @return
     */
    @GET("v1/mute/list")
    Call<MutedHistory> getMutedHistory(@Header("Authorization") String token);


    //获取好P友
    @GET("v1/user/mypixiv?filter=for_android")
    Call<ListUser> getNiceFriend(@Header("Authorization") String token,
                                       @Query("user_id") int user_id);

    //获取最新作品
    @GET("v1/illust/new?filter=for_android")
    Call<ListIllust> getNewWorks(@Header("Authorization") String token,
                                       @Query("content_type") String content_type);

    //获取最新作品
    @GET("v1/novel/new")
    Call<ListNovel> getNewNovels(@Header("Authorization") String token);


    //获取好P友
    @GET("v1/novel/text")
    Call<NovelDetail> getNovelDetail(@Header("Authorization") String token,
                                     @Query("novel_id") int novel_id);


    //获取好P友
    @GET("v1/user/me/state")
    Call<UserState> getAccountState(@Header("Authorization") String token);

    @Multipart
    @POST("v1/user/profile/edit")
    Call<NullResponse> updateUserProfile(@Header("Authorization") String token,
                                               @Part List<MultipartBody.Part> parts);


    @GET("v1/live/list")
    Call<ListLive> getLiveList(@Header("Authorization") String token,
                               @Query("list_type") String list_type);

    @GET("v1/illust/bookmark/users?filter=for_android")
    Call<ListSimpleUser> getUsersWhoLikeThisIllust(@Header("Authorization") String token,
                                                   @Query("illust_id") int illust_id);

    @GET("v2/novel/series")
    Call<ListNovelOfSeries> getNovelSeries(@Header("Authorization") String token,
                                           @Query("series_id") int series_id);

    @GET("v2/novel/detail")
    Call<NovelSearchResponse> getNovelByID(@Header("Authorization") String token,
                                           @Query("novel_id") int novel_id);

    @GET("v1/illust/series?filter=for_android")
    Call<ListMangaOfSeries> getMangaSeriesById(@Header("Authorization") String token,
                                               @Query("illust_series_id") int illust_series_id);


    @GET("v1/user/illust-series")
    Call<ListMangaSeries> getUserMangaSeries(@Header("Authorization") String token,
                                             @Query("user_id") int user_id);


    @GET("v1/user/novel-series")
    Call<ListNovelSeries> getUserNovelSeries(@Header("Authorization") String token,
                                             @Query("user_id") int user_id);

    @FormUrlEncoded
    @POST("v1/user/workspace/edit")
    Call<NullResponse> editWorkSpace(@Header("Authorization") String token,
                                           @FieldMap HashMap<String, String> fields);


    @GET("v1/user/profile/presets")
    Call<Preset> getPresets(@Header("Authorization") String token);

    @GET("v2/illust/mypixiv")
    Call<ListIllust> getNiceFriendIllust(@Header("Authorization") String token);

    @GET("v1/novel/mypixiv")
    Call<ListNovel> getNiceFriendNovel(@Header("Authorization") String token);


    @GET
    Call<ListNovelSeries> getNextUserNovelSeries(@Header("Authorization") String token,
                                                       @Url String next_url);

    @GET
    Call<ListMangaSeries> getNextUserMangaSeries(@Header("Authorization") String token,
                                                       @Url String next_url);

    @GET
    Call<ListUser> getNextUser(@Header("Authorization") String token,
                                     @Url String next_url);

    @GET
    Call<ListSimpleUser> getNextSimpleUser(@Header("Authorization") String token,
                                                 @Url String next_url);


    @GET
    Call<ListIllust> getNextIllust(@Header("Authorization") String token,
                                         @Url String next_url);

    @GET
    Call<ListNovel> getNextNovel(@Header("Authorization") String token,
                                       @Url String next_url);

    @GET
    Call<ListNovelOfSeries> getNextSeriesNovel(@Header("Authorization") String token,
                                                     @Url String next_url);

    @GET
    Call<ListArticle> getNextArticals(@Header("Authorization") String token,
                                            @Url String next_url);
}

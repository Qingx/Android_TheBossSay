package net.cd1369.tbs.android.data.repository;


import android.text.TextUtils;

import net.cd1369.tbs.android.config.DataConfig;
import net.cd1369.tbs.android.config.TbsApi;
import net.cd1369.tbs.android.config.UserConfig;
import net.cd1369.tbs.android.data.entity.ArticleEntity;
import net.cd1369.tbs.android.data.entity.FavoriteEntity;
import net.cd1369.tbs.android.data.entity.HistoryEntity;
import net.cd1369.tbs.android.data.entity.PortEntity;
import net.cd1369.tbs.android.data.entity.TokenEntity;
import net.cd1369.tbs.android.data.entity.UserEntity;
import net.cd1369.tbs.android.data.entity.VersionEntity;
import net.cd1369.tbs.android.data.service.UserService;

import java.util.List;

import cn.wl.android.lib.config.WLConfig;
import cn.wl.android.lib.core.Page;
import cn.wl.android.lib.core.PageParam;
import cn.wl.android.lib.data.repository.BaseApi;
import cn.wl.android.lib.data.repository.BaseRepository;
import cn.wl.android.lib.miss.LoginMiss;
import cn.wl.android.lib.utils.Lists;
import io.reactivex.Observable;
import okhttp3.RequestBody;

/**
 * Created by Xiang on 2021/6/22 15:21
 *
 * @description
 * @email Cymbidium@outlook.com
 */
public class UserRepository extends BaseRepository<UserService> {

    public UserRepository() {
    }

    /**
     * 游客登录
     *
     * @param tempId
     * @return
     */
    public Observable<TokenEntity> obtainTempLogin(String tempId) {
        RequestBody body = bodyFromCreator(jo -> {
            jo.put("deviceId", tempId);
        });

        return getService().obtainTempLogin(body)
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 刷新用户信息
     *
     * @return
     */
    public Observable<TokenEntity> obtainRefreshUser() {
        return getService().obtainRefresh()
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 刷新用户信息
     *
     * @return
     */
    public Observable<TokenEntity> refreshToken() {
        UserEntity entity = UserConfig.get().getUserEntity();
        String id = entity.getId();

        if (TextUtils.isEmpty(id)) {
            return Observable.error(new LoginMiss());
        }

        return getService().obtainRefresh(id)
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 发送验证码
     *
     * @param phone
     * @param type  0 登录 1验证当前手机号 2修改手机号 3绑定手机号
     * @return
     */
    public Observable<String> obtainSendCode(String phone, int type) {
        RequestBody body = bodyFromCreator(jo -> {
            jo.put("call", phone);
            jo.put("type", type);
        });

        return getService().obtainSendCode(body)
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 验证码登录
     *
     * @param phone
     * @param code
     * @return
     */
    public Observable<TokenEntity> obtainSignPhone(String phone, String code, String rnd) {
        RequestBody body = bodyFromCreator(jo -> {
            jo.put("call", phone);
            jo.put("code", code);
            jo.put("deviceId", DataConfig.get().getTempId());
            jo.put("rnd", rnd);
        });

        return getService().obtainSignPhone(body)
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 微信授权登录
     *
     * @param code
     * @return
     */
    public Observable<TokenEntity> obtainSignWechat(String code) {
        RequestBody body = bodyFromCreator(jo -> {
            jo.put("code", code);
            jo.put("deviceId", DataConfig.get().getTempId());
        });

        return getService().obtainSignWechat(body)
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 微信绑定
     *
     * @param code
     * @return
     */
    public Observable<TokenEntity> obtainBindWechat(String code) {
        RequestBody body = bodyFromCreator(jo -> {
            jo.put("code", code);
            jo.put("deviceId", DataConfig.get().getTempId());
        });

        return getService().obtainBindWechat(body)
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 修改账号昵称
     *
     * @param name
     * @return
     */
    public Observable<Boolean> obtainChangeName(String name) {
        RequestBody body = bodyFromCreator(jo -> {
            jo.put("nickName", name);
        });

        return getService().obtainChangeName(body)
                .compose(combine())
                .compose(success());
    }

    /**
     * 验证当前手机号
     *
     * @param phone
     * @param code
     * @return
     */
    public Observable<Boolean> obtainConfirmPhone(String phone, String code, String rnd) {
        RequestBody body = bodyFromCreator(jo -> {
            jo.put("call", phone);
            jo.put("code", code);
            jo.put("rnd", rnd);
        });

        return getService().obtainConfirmPhone(body)
                .compose(combine())
                .compose(success());
    }

    /**
     * 修改手机号
     *
     * @param phone
     * @param code
     * @return
     */
    public Observable<Boolean> obtainChangePhone(String phone, String code, String rnd) {
        RequestBody body = bodyFromCreator(jo -> {
            jo.put("call", phone);
            jo.put("code", code);
            jo.put("rnd", rnd);
        });

        return getService().obtainChangePhone(body)
                .compose(combine())
                .compose(success());
    }

    /**
     * 绑定手机号
     *
     * @param phone
     * @param code
     * @return
     */
    public Observable<Boolean> obtainBindPhone(String phone, String code, String rnd) {
        RequestBody body = bodyFromCreator(jo -> {
            jo.put("call", phone);
            jo.put("code", code);
            jo.put("rnd", rnd);
        });

        return getService().obtainBindPhone(body)
                .compose(combine())
                .compose(success());
    }

    /**
     * 获取用户收藏夹列表
     *
     * @return
     */
    public Observable<List<FavoriteEntity>> obtainFavoriteList() {
        return getService().obtainFavoriteList()
                .compose(combine())
                .compose(rebase());
    }


    /**
     * 创建收藏夹
     *
     * @param name
     * @return
     */
    public Observable<FavoriteEntity> obtainCreateFavorite(String name) {
        RequestBody body = bodyFromCreator(jo -> {
            jo.put("name", name);
        });

        return getService().obtainCreateFavorite(body)
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 删除收藏夹
     *
     * @param id
     * @return
     */
    public Observable<Boolean> obtainRemoveFolder(String id) {
        RequestBody body = bodyFromCreator(jo -> {
            jo.put("id", id);
        });

        return getService().obtainRemoveFolder(body)
                .compose(combine())
                .compose(success());
    }

    /**
     * 取消收藏文章
     *
     * @param id
     * @return
     */
    public Observable<Boolean> obtainCancelFavoriteArticle(String id) {
        RequestBody body = bodyFromCreator(jo -> {
            jo.put("sourceId", id);
            jo.put("target", false);
            jo.put("type", 1);
        });

        return getService().obtainOptionArticle(body)
                .compose(combine())
                .compose(success());
    }

    /**
     * 收藏文章
     *
     * @param folderId
     * @param articleId
     * @return
     */
    public Observable<Boolean> obtainFavoriteArticle(String folderId, String articleId) {
        RequestBody body = bodyFromCreator(jo -> {
                    jo.put("groupId", folderId);
                    jo.put("sourceId", articleId);
                    jo.put("target", true);
                    jo.put("type", 1);
                }
        );
        return getService().obtainOptionArticle(body)
                .compose(combine())
                .compose(success());
    }

    /**
     * 阅读记录
     *
     * @param pageParam
     * @param today
     * @return
     */
    public Observable<Page<HistoryEntity>> obtainHistory(PageParam pageParam, boolean today) {
        RequestBody body = bodyFromCreator(pageParam, jo -> {
            jo.put("today", today);
        });

        return getService().obtainHistory(body)
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 删除阅读记录
     *
     * @param id
     * @return
     */
    public Observable<Boolean> obtainRemoveHistory(String id) {
        return getService().obtainRemoveHistory(id)
                .compose(combine())
                .compose(success());
    }

    /**
     * 阅读文章
     *
     * @param id
     * @return
     */
    public Observable<Boolean> obtainReadArticle(String id) {
        return getService().obtainReadArticle(id)
                .compose(combine())
                .compose(success());
    }

    /**
     * 检查版本
     *
     * @param version
     * @return
     */
    public Observable<VersionEntity> obtainCheckUpdate(String version) {
//        if (WLConfig.isDebug()) {
//            version = "1.0.0";
//        }
        return getService().obtainCheckUpdate(version)
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 获取审核状态
     *
     * @return
     */
    public Observable<PortEntity> obtainPortStatus() {
        return getService().obtainPortStatus()
                .compose(combine())
                .compose(rebase());
    }

}

package net.cd1369.tbs.android.ui.start

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.data.core.HttpConfig
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.Gsons
import cn.wl.android.lib.utils.Toasts
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_guide.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.data.entity.UserEntity
import net.cd1369.tbs.android.ui.adapter.GuideInfoAdapter
import net.cd1369.tbs.android.ui.home.HomeActivity
import net.cd1369.tbs.android.util.Tools
import net.cd1369.tbs.android.util.Tools.logE
import net.cd1369.tbs.android.util.doClick

class GuideActivity : BaseActivity() {
    companion object {
        val JSONArray = """[
            {
                "id": "1412617791843872770",
                "name": "马化腾",
                "role": "腾讯公司董事会主席兼首席执行官",
                "head":
                "https://ss1.baidu.com/-4o3dSag_xI4khGko9WTAnF6hhy/baike/w%3D268/sign=153b390d3bdbb6fd255be2203126aba6/b219ebc4b74543a9c6c7773a1c178a82b8011463.jpg"
            },
            {
                "id": "1412618648882786306",
                "name": "马云",
                "role": "阿里巴巴集团创始人",
                "head":
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fdingyue.ws.126.net%2Fnphh%3DuwwDE2xHWCXTJ8peWswO1TSCvMzsb9QHDULPWXpV1560092282821.jpg&refer=http%3A%2F%2Fdingyue.ws.126.net&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1628660434&t=4d49ece7c550e68f4872340218a232bf"
            },
            {
                "id": "1412619182679273474",
                "name": "任正非",
                "role": "华为董事、CEO",
                "head":
                "https://www-file.huawei.com/-/media/corp2020/abouthuawei/executives/2020/renzhengfei-2020-detail.jpg"
            },
            {
                "id": "1412619857827999746",
                "name": "柳传志",
                "role": "联想控股、联想集团创始人",
                "head":
                "https://bkimg.cdn.bcebos.com/pic/a50f4bfbfbedab64c1495f14fc36afc379311e2e?x-bce-process=image/watermark,image_d2F0ZXIvYmFpa2UyNzI=,g_7,xp_5,yp_5/format,f_auto"
            },
            {
                "id": "1412659731360657409",
                "name": "史玉柱",
                "role": "商人、企业家",
                "head":
                "https://bkimg.cdn.bcebos.com/pic/6f061d950a7b02087bf4b2a03392e5d3572c11df6214?x-bce-process=image/watermark,image_d2F0ZXIvYmFpa2UxNTA=,g_7,xp_5,yp_5/format,f_auto"
            },
            {
                "id": "1412660078254764034",
                "name": "潘石屹",
                "role": "SOHO中国董事长",
                "head":
                "http://static.ws.126.net/stock/2013/8/14/2013081411234803374.jpg"
            },
            {
                "id": "1412660543440826370",
                "name": "许家印",
                "role": "中国恒大集团董事局主席、党委书记，管理学教授、博士生导师",
                "head":
                "https://bkimg.cdn.bcebos.com/pic/d043ad4bd11373f05b4756b0ad0f4bfbfbed04a0?x-bce-process=image/watermark,image_d2F0ZXIvYmFpa2U5Mg==,g_7,xp_5,yp_5/format,f_auto"
            },
            {
                "id": "1412661073705709569",
                "name": "比尔·盖茨",
                "role": "微软董事长、CEO和首席软件设计师",
                "head":
                "https://bkimg.cdn.bcebos.com/pic/9c16fdfaaf51f3de36318c129beef01f3b2979c0?x-bce-process=image/watermark,image_d2F0ZXIvYmFpa2UxNTA=,g_7,xp_5,yp_5/format,f_auto"
            }
            ]""".trimEnd()

        fun start(context: Context?) {
            val intent = Intent(context, GuideActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_guide
    }

    private fun getBossList(): MutableList<BossInfoEntity> {
        val type = object : TypeToken<List<BossInfoEntity>>() {}.type
        return Gsons.getGson().fromJson(JSONArray, type)
    }

    @SuppressLint("SetTextI18n")
    override fun initViewCreated(savedInstanceState: Bundle?) {
        countdown(5) {
            text_time.text = "${it}s"

            if (it <= 0) {
                text_time.text = "跳过"
            }
        }

        val adapter = object : GuideInfoAdapter() {
            override fun onAddFollow(data: MutableList<String>) {
                showLoadingAlert("搜寻并追踪...")

                var entity: UserEntity? = null

                val tempId = Tools.createTempId()
                TbsApi.user().obtainTempLogin(tempId).flatMap {
                    DataConfig.get().tempId = tempId
                    HttpConfig.saveToken(it.token)
                    entity = it.userInfo

                    TbsApi.boss().obtainGuideFollow(data)
                }.bindDefaultSub(doNext = {
                    Toasts.show("追踪成功")
                    hideLoadingAlert()

                    entity!!.collectNum = data.size
                    UserConfig.get().userEntity = entity

                    DataConfig.get().firstUse = false
                    HomeActivity.start(mActivity)
                    mActivity?.finish()
                }, doFail = {
                    hideLoadingAlert()
                    it.msg.logE()
                    Toasts.show(it.msg)
                })
            }
        }

        rv_content.adapter = adapter
        rv_content.layoutManager =
            object : GridLayoutManager(mActivity, 2, RecyclerView.HORIZONTAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }

        adapter.setNewData(getBossList())

        layout_add doClick {
            adapter.addFollow()
        }

        text_clear doClick {
            adapter.clearAll()
        }

        text_time doClick {
            if (text_time.text == "跳过") {
                DataConfig.get().firstUse = false
                HomeActivity.start(mActivity)
                mActivity?.finish()
            }
        }
    }
}
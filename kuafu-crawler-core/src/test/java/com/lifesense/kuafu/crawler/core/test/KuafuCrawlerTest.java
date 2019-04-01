package com.lifesense.kuafu.crawler.core.test;

import com.alibaba.fastjson.JSON;
import com.lifesense.base.spring.InstanceFactory;
import com.lifesense.kuafu.crawler.core.processor.plugins.downloader.ToutiaoDownloader;
import com.lifesense.kuafu.crawler.core.processor.plugins.filters.BaseImgCrawlerFilter;
import com.lifesense.kuafu.crawler.encode.html.HtmlParserUtils;
import com.lifesense.kuafu.crawler.encode.tag.parser.TagParserEnum;
import com.lifesense.kuafu.crawler.core.base.BaseTest;
import com.lifesense.kuafu.crawler.core.constants.CrawlerCommonConstants;
import com.lifesense.kuafu.crawler.core.processor.TestPageProcessor;
import com.lifesense.kuafu.crawler.core.processor.converter.HtmlTagFilterConverter;
import com.lifesense.kuafu.crawler.core.processor.converter.StringTrimCrawlerConverter;
import com.lifesense.kuafu.crawler.core.processor.iface.ICrawlerFilter;
import com.lifesense.kuafu.crawler.core.processor.plugins.proxy.DefaultSite;
import com.lifesense.kuafu.crawler.core.processor.plugins.filters.CrawlerFilterFactory;
import com.lifesense.kuafu.crawler.core.processor.spider.SpiderFactory;
import com.lifesense.kuafu.crawler.core.processor.utils.DomainTagUtils;
import com.lifesense.kuafu.crawler.core.processor.utils.JavaScriptExcutorUtils;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.selector.Xpath2Selector;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KuafuCrawlerTest extends BaseTest {

    @Test
    public void test() throws InterruptedException {
        InstanceFactory.getInstance("kuafuCrawlerJedisProviderFactoryBean");
        SpiderFactory.initSpider("toutiao5495166052");
        SpiderFactory.startSpider("toutiao5495166052");
        while (true) {
            if (SpiderFactory.getSpider("toutiao5495166052").spiderIsRun()) {
                System.out.println(123);
                Thread.sleep(1000l);
            }
        }
    }

    @Test
    public void test2() {
        List<ICrawlerFilter> results = CrawlerFilterFactory.getMiddleFilters("iyiou.json");
        System.out.println(results);
    }

    @Test
    public void test3() {
        List<String> allDomainTags = DomainTagUtils.getAllDomainTags();
        if (CollectionUtils.isNotEmpty(allDomainTags)) {
            for (String domainTag : allDomainTags) {
                SpiderFactory.initSpider(domainTag);
            }
        }
    }

    @Test
    public void javascriptTest() {
        Object data = "IPO 新美大 BP 商业计划书 ";
        String script = "function dataConverter(data){ if(null==data){return data}; return data.split('\\?,| ');}";
        // Object data = Arrays.asList("123", "456");
        // String script =
        // "function dataConverter(data){ if(null==data){return data}; var returnStr='';for(var i=0;i<data.size();i++){var str=data.get(i);returnStr=returnStr+str;} return returnStr;}";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CrawlerCommonConstants.JavaScriptConstant.PARAM_DATA, data);
        params.put(CrawlerCommonConstants.JavaScriptConstant.PARAM_METHODNAME, CrawlerCommonConstants.JavaScriptConstant.DEFAULT_METHODNAME);
        Object targetData = JavaScriptExcutorUtils.eval(script, params, null);
        System.out.println(targetData);
    }

    @Test
    public void nodeTest() {
        String url = "http://www.cffw.net/zhkcw/11344.html";
        String urlPattern = "(http://www\\.cffw\\.net/zhkcw/\\w+\\.html)";
        ResultItems result = Spider.create(new TestPageProcessor(url, "GBK", urlPattern)).get(url);
        String html = result.get("html");
        LOGGER.info(html);
        String xpath = "//div[@id='MyContent']/br[1]/preceding-sibling::text()|//div[@id='MyContent']/br[1]/preceding-sibling::*/text()";
        Xpath2Selector selector = new Xpath2Selector(xpath);
        List<String> data = selector.selectList(html);
        String script = "function dataConverter(data){ if(null==data){return data}; var returnStr='';for(var i=0;i<data.size();i++){var str=data.get(i);returnStr=returnStr+str;} return returnStr;}";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CrawlerCommonConstants.JavaScriptConstant.PARAM_DATA, data);
        params.put(CrawlerCommonConstants.JavaScriptConstant.PARAM_METHODNAME, CrawlerCommonConstants.JavaScriptConstant.DEFAULT_METHODNAME);
        Object targetData = JavaScriptExcutorUtils.eval(script, params, null);
        Page page = new Page();
        targetData = new StringTrimCrawlerConverter().converter(page, targetData, null);
        System.out.println(targetData);
    }

    @Test
    public void wordTest() {
        String word = "駅";
        try {
            String newWord = new String(word.getBytes(), "gb2312");
            System.out.println(newWord);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void htmlTest() {
        String url = "https://news.qq.com/";
        String urlPattern = "(http://www\\.cffw\\.net/zhkcw/\\w+\\.html)";
        ResultItems result = Spider.create(new TestPageProcessor(url, "GBK", urlPattern)).get(url);
        String html = result.get("html");
        String targetHtml = HtmlParserUtils.parser(html, TagParserEnum.a_parser_text);
        LOGGER.info(targetHtml);
    }

    @Test
    public void building() {
        // String html =
        // "<p style=\"white-space: normal;\"><span style=\"line-height: 1.4em;\">近日</span><span style=\"line-height: 1.4em;\">看到一条消息：新美大（</span>Internet Plus Holdings.LTD，<span style=\"line-height: 1.4em;\">美团和大众点评的合称，以下简称“新美大”）在对</span><span style=\"line-height: 1.4em;\">外宣布最新一次融</span><span style=\"line-height: 1.4em;\">资金额超过33亿美元之后，还对外放出了关于“新美大”投资事项及条件：</span><span style=\"line-height: 1.4em;\">本次转让股份额度</span><span style=\"line-height: 1.4em;\">共计7150511.13美元，占股0.04767%，</span><span style=\"line-height: 1.4em;\">估值以市场上新美大最新估值投前150亿美元为准。</span><br /></p>  <p style=\"white-space: normal;\">据亿欧网了解，所有资料在得到新美大投资方授权后呈现。这是潜力股网站公开发文时表述的部分内容，这说明，新美大释放的这部分股份是其投资方对方放出的股份，类似众筹。</p>  <p style=\"white-space: normal;\">于是乎，我以为自己的春天来了，也可以做一回新美大的微股东了。天生数学缺陷的自己，核算着自己账户存款和新美大每股的价钱，一顿狂算之后发现，自己是不是傻。按照估值直接计算即可，为何还按照新美大上文中的0.04767%股份计算。</p>  <p style=\"white-space: normal;\">明眼人一看就知道，是的，如果占有“新美大”0.01%的股份需要150万美金，更形象点就是新美大1%的股份需要1.5亿美元的投资，哈哈，暗讽自己一下，简直穷屌了！</p>  <p style=\"white-space: normal;\">不过话说，新美大此次对外散户（中小投资机构）释放股份还是比较迫切的，作为一个媒体人，我已经接到多个从业者转来的新美大股份转让的规则和明细，只是没钱没办法。</p>  <p style=\"white-space: normal;\"><strong>网上对新美大股份转让的内容如下：</strong></p>  <p style=\"white-space: normal;\">“新美大”：1+1&gt;2，打造宇宙第一O2O。<span style=\"line-height: 1.4em;\">2015年10 月8日，大众点评网与美团网宣布达成战略合并，宇宙第一O2O自此诞</span><span style=\"line-height: 1.4em;\">生。</span></p>  <p style=\"white-space: normal;\">合并后点评美团优势互补：点评在一二线城市拥有绝对优势，美团强于三四线城市的覆盖，二者合计占领O2O市场80%份额，拥有行业绝对话语权。</p>  <p style=\"white-space: normal;\"><span style=\"line-height: 1.4em;\">新美大在收入模式上将会逐渐趋于融合，广告、交易抽成和服务佣金等三大核心盈利渠道将会被新公司加强，构成新美大“钱袋子”的底仓。</span><span style=\"line-height: 1.4em;\">同时通过深耕垂直行业，深入交易和服务的全流程，帮助商家进行营销、提升效率、优化运营，为商家提供选址、融资、采购等增值服务，开拓出新的收入渠道。</span><span style=\"line-height: 1.4em;\">也就是说以后要找商户大数据、做精准营销、优化运营、选址、找采购商，都可以找新美大。</span></p>  <p style=\"white-space: normal;\"><span style=\"line-height: 1.4em;\">其投资方式：</span><span style=\"line-height: 1.4em;\">美团、大众点评合并后的新美大公司接受的投资基金规模在约7亿人民币；</span><span style=\"line-height: 1.4em;\">投资期限3+2年；</span><span style=\"line-height: 1.4em;\">认购起点100万起，10万递增，认购费1%；</span><span style=\"line-height: 1.4em;\">管理费1%/年，投顾费1%/年。</span></p>  <p style=\"white-space: normal;\"><strong>新美大的投资亮点为：</strong></p>  <p style=\"white-space: normal;\">1）增长空间巨大：中国本地生活市场规模巨大，目前本地生活O2O仍处在早期发展阶段，市场增长空间巨大；</p>  <p style=\"white-space: normal;\">2）行业统治地位：点评美团合并将使公司拥有行业中的绝对统治地位，市场占有率达到80%；</p>  <p style=\"white-space: normal;\">3）优势互补：合并后，两个平台协同效应显现，营运成本将显著降低，营收显著增加；</p>  <p style=\"white-space: normal;\">4）豪华团队：合并后的点评美团将集合本土O2O方面最资深的从业者；</p>  <p style=\"white-space: normal;\">5）强大股东资源：腾讯、红杉、DST、新加坡主权投资基金等强大股东资源将为公司提供业务拓展、营销渠道等全方位支持，机构认同度高；</p>  <p style=\"white-space: normal;\">6）估值折让优势：美团和大众点评合并前各自估值分别为（120亿+70亿美元），本轮融资超过30亿美元，投前估值150亿美元，较前一轮估值有所折让，为本轮投资者进入提高收益空间；</p>  <p style=\"white-space: normal;\">7）IPO退出预期：合并后，规模效应显现，公司盈利拐点提前到来，预计2—3年内登陆美国纳斯达克，实现IPO退出。</p>  <p style=\"white-space: normal;\"><strong>在新美大形势一片大好的呼声中，<a href=\"http://www.iyiou.com/p/23915\" target=\"_blank\">业界也有人并不太看好</a>：</strong></p>  <p style=\"white-space: normal;\">&nbsp;新美大（CIP）获得了30亿美元的融资，行业内对新美大（CIP）持乐观态度成为了主流。但实际上，新美大（CIP）以150亿美元的投前估值进行流血融资，30亿美元的融资额依然不够，屌丝群体多，单个用户产生的交易额低，在细分领域的领先优势也随时会被改写。</p>  <p style=\"white-space: normal;\">新美大对外融资时，以市销率（P/S）和P/GMV两种估值模型预测未来的市值，得出了夸张的数据，是典型的放卫星。考虑到行业属性、对标公司、市场格局等因素，等到2018年上市时，新美大（CIP）的市值在300亿美元左右更为理性。</p>  <p style=\"white-space: normal;\">美团最美好的时间是2012-2014这三年。2015年，巨额亏损的美团向投资方做了妥协，和大众点评的合并成为了理想主义和现实主义的分界线。在O2O价格战无门槛无绝期的情况下，新美大未来的结局为理性估值上市或再次合并，在BAT之外，新美大（CIP）届时的市值应该在京东之下，成为下一个阿里巴巴会是一个难以触及的梦想。</p>  <p style=\"white-space: normal;\"><span style=\"line-height: 1.4em;\">就此，我查看了截至到北京时间2016年1月23日下午三点</span><span style=\"line-height: 1.4em;\">整BAT、京东等的市值，阿里的市值为1768.41亿美元，腾讯的市</span><span style=\"line-height: 1.4em;\">值为</span><span style=\"line-height: 1.4em;\">13071.46亿港元（约合1681亿美元），百度</span><span style=\"line-height: 1.4em;\">的市值为</span><span style=\"line-height: 1.4em;\">598.75亿美元，京</span><span style=\"line-height: 1.4em;\">东市值</span><span style=\"line-height: 1.4em;\">372.80亿美元，奇虎36</span><span style=\"line-height: 1.4em;\">0的市值为</span><span style=\"line-height: 1.4em;\">87.97亿美元，聚美优品市值为</span><span style=\"line-height: 1.4em;\">9.37亿美元。</span></p>  <p style=\"white-space: normal;\"><span style=\"line-height: 1.4em;\">且据知情人士对亿欧网透露：2011年阿里入股美团前，王兴还剩50%的股份，阿里那一轮给美团的估值为2-3亿美元，融资4000多万美元，让出15%的股份，之后王兴剩42.5%股份；到2014年年终美团以20-30亿美元的估值融资近3亿美元，王兴还剩38%股份；2015年1月美团估值70亿美元获得7亿美元融资，王兴在美团还剩34%股份；2015年10月，美团大众点评合并，差不多按1:1的比例，王兴在集团里面的股份降到20%以下；2015年12月，新公司估值180亿获得腾讯领投的28亿美元，王兴占美团的股份降到15%左右。</span></p>  <p style=\"white-space: normal;\"><span style=\"line-height: 1.4em;\">从50%的股份占比降到15%经过了5年的时间，美团也从众多的团购网站中成长为一个本地生活服务巨头。</span></p>  <p style=\"white-space: normal;\"><span style=\"line-height: 1.4em;\">啊……还是没钱投美团啊！</span></p>  <p><span style=\"color: rgb(192, 0, 0);\">本文作者小瓶盖，亿欧网专栏作者；微信：canying1000（添加时请注明“姓名-公司-职务”方便备注）；转载请注明作者姓名和“来源：亿欧网”；文章内容系作者个人观点，不代表亿欧网对观点赞同或支持。</span></p>  <p class=\"PlayTourSubmit\"><img style=\"width:60px; height:60px\" src=\"http://www.iyiou.com/Public/WWW/images/shang.png\" /></p>  <input name=\"PlayTourPrice\" id=\"PlayTourPrice\" type=\"hidden\" value=\"10\" />  <p class=\"PlayTourImg\" style=\"display:none\"></p>  <p class=\"PlayTourMemo\" style=\"text-align:center;text-indent:0;margin-top:0;display:none\">扫码给TA打赏</p>";

        String html =
                "<div>  &nbsp; &nbsp; 中华  <a href=\"http://www.cffw.net\" class=\"innerlink\">快餐网</a>讯&nbsp;世界永恒不变的道理就是变，餐饮的体验也是在不断地迭代创新。麦当劳概念餐厅将自由和选择权交回顾客手中，用融合数字化、个性化和人性化的体验，回应顾客的核心用餐需求，体验、智能、个性、人性一个不能少。 </div>  <br /> &nbsp;&nbsp; &nbsp;中国大陆首家麦当劳未来智慧概念餐厅1月27日在北京王府井正式开幕，顾客可率先体验更大用餐自由，无论何时、何地、以何种方式，随心享受麦当劳的产品与服务。  <div style=\"text-align:center\">  <br /> &nbsp;  <a href=\"\" onclick=\"showimg('http://www.cffw.net/UploadFiles/2016-01/201601291656079946.jpg');\"><img src=\"http://www.cffw.net/UploadFiles/2016-01/201601291656079946.jpg\" alt=\"揭秘麦当劳第一家概念餐厅！\" onmousewheel=\"return bbimg(this)\" onload=\"javascript:resizepic(this)\" border=\"0\" /></a> </div>  <br /> &nbsp;&nbsp; &nbsp;走进位于北京王府井大街200号的概念餐厅，餐厅设计简约沉稳，格局通透开放，突出了食物的色彩。 <br /> &nbsp;  <div style=\"text-align:center\">  <a href=\"\" onclick=\"showimg('http://www.cffw.net/UploadFiles/2016-01/2016012916562510263.jpg');\"><img src=\"http://www.cffw.net/UploadFiles/2016-01/2016012916562510263.jpg\" alt=\"揭秘麦当劳第一家概念餐厅！\" onmousewheel=\"return bbimg(this)\" onload=\"javascript:resizepic(this)\" border=\"0\" /></a> </div>  <br />  <strong>&nbsp;&nbsp; &nbsp;一、开放厨房，明档操作“顾客自创汉堡”</strong> <br />  <br /> &nbsp;&nbsp; &nbsp;麦当劳新概念餐厅，有两大重要突破：一是摒弃原来的“后厨”概念，把厨房开放化，明档操作，顾客可以看到食物制作过程。二、顾客不再只能吃固定几种汉堡口味，而是可以自由搭配、私人定制。 <br />  <br /> &nbsp;&nbsp; &nbsp;这种突破式革新，代表着餐饮经营的两大方向，一是厨房开放一定是主流；二是满足顾客个性需求，是餐饮对服务更深次的要求。 <br />  <br /> &nbsp;&nbsp; &nbsp;以前，麦当劳前厅和后厨严格区分，而新版麦当劳最大的不同，就是开放式厨房、明档操作。随之相配的项目就是“我创我味来”，即明档形式制作由顾客自己搭配食材的个性化汉堡：以前麦当劳的汉堡口味是店家制定的，而现在，顾客可以自由搭配，顾客可以自己选什么口味的肉饼、什么酱料，放什么蔬菜、不放沙拉，被称之为“私人订制”。 <br />  <br /> &nbsp;&nbsp; &nbsp;现在麦当劳全球10个国家和地区已推出该项目，在中国已有11家餐厅采用开放式明档厨房来制作汉堡。 <br />  <br /> &nbsp;&nbsp; &nbsp;最重要的，这一私人定制形式一推出，大大超乎麦当劳公司的意料：原来顾客都如此愿意体验自主定制汉堡的乐趣！所以，每天排队最凶的就是私人定制区！  <div style=\"text-align:center\">  <br /> &nbsp;  <a href=\"\" onclick=\"showimg('http://www.cffw.net/UploadFiles/2016-01/2016012916565249168.jpg');\"><img src=\"http://www.cffw.net/UploadFiles/2016-01/2016012916565249168.jpg\" alt=\"揭秘麦当劳第一家概念餐厅！\" onmousewheel=\"return bbimg(this)\" onload=\"javascript:resizepic(this)\" border=\"0\" /></a> </div>  <div style=\"text-align:center\">  &nbsp;&nbsp; &nbsp;制作汉堡使用的新鲜食材一览无余 </div>  <div style=\"text-align:center\">  <br /> &nbsp;  <a href=\"\" onclick=\"showimg('http://www.cffw.net/UploadFiles/2016-01/2016012916570835293.jpg');\"><img src=\"http://www.cffw.net/UploadFiles/2016-01/2016012916570835293.jpg\" alt=\"揭秘麦当劳第一家概念餐厅！\" onmousewheel=\"return bbimg(this)\" onload=\"javascript:resizepic(this)\" border=\"0\" /></a> </div>  <div style=\"text-align:center\">  <strong>&nbsp;&nbsp; &nbsp;</strong>麦当劳开放式厨房 </div>  <br /> &nbsp;&nbsp; &nbsp;进店就餐的顾客通过手机或点餐机，采用自助方式通过6个简单的步骤，即可完成汉堡的“私人定制”，虽然49元起的定制汉堡价格不菲，但仍受到年轻人的追捧。《餐饮时报》记者在麦当劳北京王府井店看到，中午不到11:30就排起了长队，顾客都想体验下非标准化、个性化汉堡的滋味。  <div style=\"text-align:center\">  <br /> &nbsp;  <a href=\"\" onclick=\"showimg('http://www.cffw.net/UploadFiles/2013-11/2016012916572423284.jpg');\"><img src=\"http://www.cffw.net/UploadFiles/2013-11/2016012916572423284.jpg\" alt=\"揭秘麦当劳第一家概念餐厅！\" onmousewheel=\"return bbimg(this)\" onload=\"javascript:resizepic(this)\" border=\"0\" /></a> </div>  <br /> &nbsp; &nbsp;  <strong>二、服务更高效，点餐、取餐区分离</strong> <br />  <br /> &nbsp;&nbsp; &nbsp;以前来麦当劳，顾客都集中在点餐柜台前，等待点餐、等待取餐，而新概念餐厅，点餐和取餐分离，服务效率更高。 <br />  <br /> &nbsp;&nbsp; &nbsp;麦当劳概念餐厅的点餐柜台与传统点餐柜台相比： <br />  <br /> &nbsp;&nbsp; &nbsp;1.更加小巧、独立，让顾客与点餐员的距离更亲近、沟通更加容易； &nbsp; <br />  <br /> &nbsp;&nbsp; &nbsp;2.菜单、餐牌、取餐牌也不再是传统的灯箱式，而是使用动态电子屏幕，呈现的信息更多、更清晰，更可以随时更新； <br />  <br /> &nbsp; &nbsp; 3.点餐与取餐区的分离（即双点式柜台），服务更快捷，顾客不会再扎堆挤在点餐区。 <br />  <br /> &nbsp;&nbsp; &nbsp;4.更重要的是，这种双点式的柜台设计可满足手机点餐、外卖服务的需要，这为麦当劳的下一步的020的闭环奠下基石。  <div style=\"text-align:center\">  <br /> &nbsp;  <a href=\"\" onclick=\"showimg('http://www.cffw.net/UploadFiles/2016-01/2016012916573858665.jpg');\"><img src=\"http://www.cffw.net/UploadFiles/2016-01/2016012916573858665.jpg\" alt=\"揭秘麦当劳第一家概念餐厅！\" onmousewheel=\"return bbimg(this)\" onload=\"javascript:resizepic(this)\" border=\"0\" /></a> </div>  <div style=\"text-align:center\">  双点式柜台设置，动态电子屏幕清晰可见 </div>  <div style=\"text-align:center\">  <br />   <a href=\"\" onclick=\"showimg('http://www.cffw.net/UploadFiles/2016-01/2016012916584119633.jpg');\"><img src=\"http://www.cffw.net/UploadFiles/2016-01/2016012916584119633.jpg\" alt=\"揭秘麦当劳第一家概念餐厅！\" onmousewheel=\"return bbimg(this)\" onload=\"javascript:resizepic(this)\" border=\"0\" /></a> </div> &nbsp; <br /> &nbsp; &nbsp; 麦当劳反其自助化服务之道，推出送餐到桌的服务。这意味着将需要更多门店服务人员，对于当下实体餐饮企业而言，这是在增加人力成本来提高门槛的节奏。 <br />  <br /> &nbsp;  <strong>&nbsp; 三、数字化点餐，送餐到桌</strong> <br />  <br /> &nbsp;&nbsp; &nbsp;除传统柜台点餐外，麦当劳概念餐厅还提供2种数字化点餐方式： <br />  <br /> &nbsp;&nbsp; &nbsp;一种是自助点餐机，放置在餐厅的入口位置，非常醒目，无论是自创汉堡、麦咖啡，还是普通菜单，都可通过触屏滑动手指即可实现点餐。这种采用超大屏幕的自助点餐机比通常采用的IPAD点餐方式图文更清晰，也更能培养顾客的使用习惯。点餐后，可选择微信支付或刷卡，直接付款，每人还会领到一个定位器，在餐厅任何位置自由就坐后，接待员提供送餐到桌。  <div style=\"text-align:center\">  <br /> &nbsp;  <a href=\"\" onclick=\"showimg('http://www.cffw.net/UploadFiles/2016-01/20160129165857843.jpg');\"><img src=\"http://www.cffw.net/UploadFiles/2016-01/20160129165857843.jpg\" alt=\"揭秘麦当劳第一家概念餐厅！\" onmousewheel=\"return bbimg(this)\" onload=\"javascript:resizepic(this)\" border=\"0\" /></a> </div>  <div style=\"text-align:center\">  入口位置放置的4台可刷卡的触屏自助点餐机。 </div>  <div style=\"text-align:center\">  <br /> &nbsp;  <a href=\"\" onclick=\"showimg('http://www.cffw.net/UploadFiles/2016-01/2016012916591430872.jpg');\"><img src=\"http://www.cffw.net/UploadFiles/2016-01/2016012916591430872.jpg\" alt=\"揭秘麦当劳第一家概念餐厅！\" onmousewheel=\"return bbimg(this)\" onload=\"javascript:resizepic(this)\" border=\"0\" /></a> </div>  <div style=\"text-align:center\">  定位器，根据定位器提供的位置信息提供送餐到桌服务 </div>  <br /> &nbsp;&nbsp; &nbsp;第二种点餐方式是微信手机点餐，在餐厅二楼的部分区域的桌面上贴有二维码，扫码后点击“自创汉堡”或“我要加餐”，无论是定制汉堡还是在用餐时想加点饮料、小食或甜点，不用离开座位，用手机下单，根据二维码上自带的位置信息，便会有专人免费送餐到桌。  <div style=\"text-align:center\">  <br /> &nbsp;  <a href=\"\" onclick=\"showimg('http://www.cffw.net/UploadFiles/2016-01/2016012916593183640.jpg');\"><img src=\"http://www.cffw.net/UploadFiles/2016-01/2016012916593183640.jpg\" alt=\"揭秘麦当劳第一家概念餐厅！\" onmousewheel=\"return bbimg(this)\" onload=\"javascript:resizepic(this)\" border=\"0\" /></a> </div>  <div style=\"text-align:center\">  与微信合作，麦当劳推出的手机点餐界面 </div>  <br />  <strong>&nbsp;&nbsp; &nbsp;四、更重体验式服务，增大亲子游乐场</strong> <br />  <br /> &nbsp;&nbsp; &nbsp;麦当劳新概念餐厅还有一项重大推进，为儿童提供的不仅仅是传统的生日派对服务和儿童游戏区，在餐厅二楼，麦当劳推出全国首个“亲子游戏互动空间”，有5款“开心寻宝乐园”线上游戏和3款线下游戏。 <br />  <br /> &nbsp;&nbsp; &nbsp;打开手机蓝牙，微信摇一摇即可发现“开心寻宝乐园”，里面有5款基于地理位置的适合三至九岁儿童的益智游戏，如涂鸦、讲故事、酷跑和消除等，游戏闯关成功还可以兑换美食。 <br />  <br /> &nbsp;&nbsp; &nbsp;三款线下游戏分别是“转迷宫”、“打气泡”和“电子互动墙”，让孩子在游戏的同时也得到了身体的锻炼。  <div style=\"text-align:center\">  <br /> &nbsp;  <a href=\"\" onclick=\"showimg('http://www.cffw.net/UploadFiles/2016-01/2016012917001868341.jpg');\"><img src=\"http://www.cffw.net/UploadFiles/2016-01/2016012917001868341.jpg\" alt=\"揭秘麦当劳第一家概念餐厅！\" onmousewheel=\"return bbimg(this)\" onload=\"javascript:resizepic(this)\" border=\"0\" /></a> </div>  <br /> &nbsp; &nbsp; 在“开心寻宝乐园”里，有五款适合三至九岁儿童的益智游戏，如涂鸦、讲故事、酷跑和消除等，融入麦当劳趣味元素，游戏积分还可兑换美食。餐厅二楼还有转迷宫、打气泡等互动游戏，让孩子动起来。  <div style=\"text-align:center\">  <br />   <a href=\"\" onclick=\"showimg('http://www.cffw.net/UploadFiles/2016-01/2016012917003364746.jpg');\"><img src=\"http://www.cffw.net/UploadFiles/2016-01/2016012917003364746.jpg\" alt=\"揭秘麦当劳第一家概念餐厅！\" onmousewheel=\"return bbimg(this)\" onload=\"javascript:resizepic(this)\" border=\"0\" /></a> </div>  <div>  &nbsp; </div>  <div>  &nbsp; &nbsp;   <strong>五、更重人性化服务，手机可无线充电</strong> </div>  <br /> &nbsp;&nbsp; &nbsp;以后在麦当劳新概念餐厅用餐，再也不用担心手机没电了！ <br />  <br /> &nbsp;&nbsp; &nbsp;因为概念餐厅提供免费无线手机充电站，这个充电设备就安装在餐桌上，你可以一边吃汉堡、一边刷朋友圈，一边充电！只要把手机放上轻轻一贴，不需要任何插头，就可实现自动充电。  <div style=\"text-align:center\">  <br />   <a href=\"\" onclick=\"showimg('http://www.cffw.net/UploadFiles/2016-01/0/2016012917023490481.jpg');\"><img src=\"http://www.cffw.net/UploadFiles/2016-01/0/2016012917023490481.jpg\" alt=\"揭秘麦当劳第一家概念餐厅！\" onmousewheel=\"return bbimg(this)\" onload=\"javascript:resizepic(this)\" border=\"0\" /></a> </div> &nbsp; <br /> &nbsp;&nbsp; &nbsp; <strong>六、独立派对房，微信照片免费打印</strong> <br />  <br /> &nbsp;&nbsp; &nbsp;麦当劳概念餐厅在二楼中央设置了可容纳70多人的独立派对房，专供一些小型聚会、派对使用，有独立空间，不受其他顾客打扰。 <br />  <br /> &nbsp;&nbsp; &nbsp;更个性的服务是可以用微信上传照片，并打印出来，而且是免费的！  <div style=\"text-align:center\">  <br /> &nbsp;  <a href=\"\" onclick=\"showimg('http://www.cffw.net/UploadFiles/2016-01/2016012917030036941.jpg');\"><img src=\"http://www.cffw.net/UploadFiles/2016-01/2016012917030036941.jpg\" alt=\"揭秘麦当劳第一家概念餐厅！\" onmousewheel=\"return bbimg(this)\" onload=\"javascript:resizepic(this)\" border=\"0\" /></a> </div>  <div style=\"text-align:center\">  70个餐位的派对房 </div>  <div style=\"text-align:center\">  <br /> &nbsp;  <a href=\"\" onclick=\"showimg('http://www.cffw.net/UploadFiles/2016-01/2016012917032511620.jpg');\"><img src=\"http://www.cffw.net/UploadFiles/2016-01/2016012917032511620.jpg\" alt=\"揭秘麦当劳第一家概念餐厅！\" onmousewheel=\"return bbimg(this)\" onload=\"javascript:resizepic(this)\" border=\"0\" /></a> </div>  <br /> &nbsp;&nbsp; &nbsp;微信照片打印机，每位顾客可免费打印一张照片。 <br />  <br /> &nbsp;  <strong>&nbsp; 七、智能灯光，自动调节更舒适</strong> <br />  <br /> &nbsp;&nbsp; &nbsp;麦当劳餐厅的灯光可以随时根据自然光线的明暗自动调整亮度，不仅环保，而且光线始终调节最适合用餐的状态。 <br />  <br /> &nbsp;&nbsp; &nbsp;原来，概念餐厅的灯光照明经过了智能化设计，根据自然光线的明暗自动调节，白天明亮，夜晚柔和，提供更舒适的就餐环境。 <br /> &nbsp;  <div style=\"text-align:center\">  <a href=\"\" onclick=\"showimg('http://www.cffw.net/UploadFiles/2016-01/2016012917042440090.jpg');\"><img src=\"http://www.cffw.net/UploadFiles/2016-01/2016012917042440090.jpg\" alt=\"揭秘麦当劳第一家概念餐厅！\" onmousewheel=\"return bbimg(this)\" onload=\"javascript:resizepic(this)\" border=\"0\" /></a> </div>  <br />  <strong>&nbsp;&nbsp; &nbsp;八、高速干手器，细节的力量</strong> <br />  <br /> &nbsp;&nbsp; &nbsp;麦当劳概念餐厅使用英国戴森公司出品的干手器，不仅风力大，在12秒之内达到干手效果，而且洗手、干手在洗手池内一步到位，更可实现消毒功能，杀灭99.9%的细菌。  <div style=\"text-align:center\">  <br /> &nbsp;  <a href=\"\" onclick=\"showimg('http://www.cffw.net/UploadFiles/2016-01/2016012917051860506.jpg');\"><img src=\"http://www.cffw.net/UploadFiles/2016-01/2016012917051860506.jpg\" alt=\"揭秘麦当劳第一家概念餐厅！\" onmousewheel=\"return bbimg(this)\" onload=\"javascript:resizepic(this)\" border=\"0\" /></a> </div>  <br />  <strong>&nbsp; &nbsp; 九、麦咖啡，MINI版咖啡厅</strong> <br />  <br /> &nbsp;&nbsp; &nbsp;概念餐厅中的“麦咖啡”除提供咖啡、茶饮外，还提供多达12款手工面包和6款马卡龙，就提供的产品而言，已经具备一个独立咖啡厅的资格。 <br /> 所以，以后不仅仅来用餐，还可以来专门喝咖啡！  <div style=\"text-align:center\">  <br /> &nbsp;  <a href=\"\" onclick=\"showimg('http://www.cffw.net/UploadFiles/2016-01/2016012917054762698.jpg');\"><img src=\"http://www.cffw.net/UploadFiles/2016-01/2016012917054762698.jpg\" alt=\"揭秘麦当劳第一家概念餐厅！\" onmousewheel=\"return bbimg(this)\" onload=\"javascript:resizepic(this)\" border=\"0\" /></a> </div>  <br /> &nbsp; &nbsp; 另外，“汉堡吧”（Burger Bar）采用了开放式厨房的设计，新鲜食材、汉堡烹饪的过程一览无余，新鲜看得见，顾客也能亲眼见证自己专属汉堡的诞生。 <br />  <br /> &nbsp; &nbsp; <strong> 十、极简设计，通透、开放空间</strong> <br />  <br /> &nbsp;&nbsp; &nbsp;麦当劳概念餐厅的设计采用了灰色中性色调，大面积的使用黄色、红色玻璃，营造出通透、开放的空间效果，与香港麦当劳概念餐厅一样，都出自同一澳大利亚顶尖设计团队之手。  <div style=\"text-align:center\">  <br />   <a href=\"\" onclick=\"showimg('http://www.cffw.net/UploadFiles/2016-01/2016012917060364599.jpg');\"><img src=\"http://www.cffw.net/UploadFiles/2016-01/2016012917060364599.jpg\" alt=\"揭秘麦当劳第一家概念餐厅！\" onmousewheel=\"return bbimg(this)\" onload=\"javascript:resizepic(this)\" border=\"0\" /></a> </div> &nbsp; <br /> &nbsp; &nbsp; 麦当劳概念餐厅的十项创新，背后遵循了“数字化、个性化、人性化”的主线，与微信联手进行的多项数字化的革新，更反映了麦当劳对中国市场的高度重视，适应本土化客群的需求的努力；自创汉堡等举措把自由和选择权重新交回顾客手中，高质高价的定价策略也使得这一个性化的改革变得有利可图；诸如装修风格的改变、充电站、智能灯光系统、高速干手器等人性化的改造，对应着顾客深层次的用餐需求。 <br />  <br /> &nbsp;&nbsp; &nbsp;麦当劳的首家概念餐厅，引领着快餐连锁的变革方向，具有十分重要的示范意义，对于中国的快餐连锁企业会带来革命性启迪。 <br />  <br /> &nbsp; &nbsp; 麦当劳（中国）有限公司首席执行官张家茵女士表示：“北京王府井餐厅对麦当劳意义非凡。1992年，我们在此开设了当时全球最大的麦当劳餐厅。2016年，与微信联手合作的全国第一家未来智慧概念餐厅又在此诞生。";
        String targetHtml = HtmlParserUtils.parser(html, TagParserEnum.a_parser_text);
        targetHtml = HtmlParserUtils.parser(targetHtml, TagParserEnum.img_parser_url);
        LOGGER.info(targetHtml);
    }

    @Test
    public void jsonTest() {
        String data =
                "{\"descUrl\":\"http://evt.dianping.com/midas/bonus/qa.html\",\"useOne\":\"true\",\"shareTitle\":\"四川香天下火锅100元福利大派送，速度来领！\",\"bonusLimit\":5,\"shareDesc\":\"大众点评喊你领福利啦，呼唤小伙伴立即分享和领取！\",\"messagePush\":false,\"totalLimit\":3000,\"bonusValidDay\":100,\"claimType\":1,\"perLimit\":10,\"description\":\"温馨提示会员务必知悉\n 1:此红包只可抵用菜金（锅底、酒水饮料、调料、小吃除外） 2:只能堂吃哦（不可外食）～ 3:不与其他优惠同享哦～ 4:仅限周一至周四哦～\",\"value\":100,\"branchName\":\"四川香天下火锅\",\"logoImgUrl\":\"/pc/promo/4a053d5fe23484468c211ea7bf70e3f6\",\"appPush\":false,\"shareImgUrl\":\"http://i1.s1.dpfile.com/pc/promo/bb53f45667da2fea5d12ea8d2eeaee91(85c85)/thumb.jpg\",\"needCode\":false}";
        try {
            // JSONObject json = new JSONObject(data);

            com.alibaba.fastjson.JSONObject alJson = JSON.parseObject(data);
            String result = (String) alJson.get("descUrl");
            System.out.println(result);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void aTagParseTest() {

        String url = "<a href='http://tech.ifeng.com/listpage/803/2/list.shtml?cflag=1&cursorId=41564925'></a>";
        // String urlPattern = "(http://www\\.hupogu\\.com/topics/\\d+)";
        // ResultItems result = Spider.create(new TestPageProcessor(url, "GBK",
        // urlPattern)).get(url);
        // String html = result.get("html");
        // LOGGER.info(html);
        // BaseImgCrawlerFilter filter = new BaseImgCrawlerFilter();
        // Page page = new Page();
        // page.setRawText(html);
        // page.setRequest(new Request(url));
        // filter.doFilter(page);

        String urlInfo = HtmlParserUtils.parser(url, TagParserEnum.a_remove_param);

        LOGGER.info(urlInfo);
        // LOGGER.info(page.getRawText());
    }

    @Test
    public void imgParseTest() {
        String html = "<div>\\n <p>小编有一段时间没有给大家说说高血压降压药物了，今天打算借着两种降压药物给大家说一说高血压用药的一些细节。实际上，我们治疗高血压的主要目的是最大限度的降低心血管发病和死亡的总危险。由于心血管病危险性与血压之间的相关呈连续性，因此抗高血压治疗的目标是将血压恢复至正常水平。</p>\\n <p>今天要说的两种降压药物是马来酸依那普利与非洛地平，马来酸依那普利属于血管紧张素转换酶抑制剂，功能主要用于各期原发型高血压、肾性高血压、充血性心力衰竭等的治疗。非洛地平属于二氢砒啶类钙拮抗剂，主要用于轻、中度原发性高血压的治疗。</p>\\n <div class=\\\"pgc-img\\\">\\n  <img src=\\\"http://p9.pstatp.com/large/pgc-image/1528869549146363716b52d\\\" img_width=\\\"323\\\" img_height=\\\"201\\\" alt=\\\"马来酸依那普利与非洛地平，高血压用药细节要注意！\\\" inline=\\\"0\\\" />\\n  <p class=\\\"pgc-img-caption\\\"></p>\\n </div>\\n <p>马来酸依那普利常用剂量为每日10~40mg，分2~3次服。原发型高血压：每次20mg，每日1次。充血性心力衰竭和肾性高血压：每日10~40mg，起始量为每日10mg，剂量视治疗效果调节，一般每次20mg，每日1次。其不良反应较少。少数出现干咳、眩晕、头痛、疲乏、腹泻、皮疹、味觉障碍、均轻微、短暂。罕有神经血管性水肿，如出现应立即停用，并迅速就医。</p>\\n <p>非洛地平起始剂量2.5mg，一日2次，或遵医嘱。常用维持剂量每日为5mg 或10mg，必要时剂量可进一步增加，或加用其它降压药。服药应早晨用水吞服。非洛地平和其它钙拮抗药相同，在某些病人身上会导致面色潮红、头痛、头晕、心悸和疲劳，这些反应大部分具有剂量依赖性，而且是在剂量增加后开始的短时间内出现，是暂时的，应用时间延长后消失。另外非洛地平可引起与剂量有关的踝肿、牙龈或牙周炎，患者用药后可能会引起轻微的牙龈肿大。在极少数病人中可能会引起显著的低血压伴心动过速，这在易感个体可能会引起心肌缺氧。</p>\\n <div class=\\\"pgc-img\\\">\\n  <img src=\\\"http://p1.pstatp.com/large/pgc-image/1528869620791a29f93ee74\\\" img_width=\\\"800\\\" img_height=\\\"800\\\" alt=\\\"马来酸依那普利与非洛地平，高血压用药细节要注意！\\\" inline=\\\"0\\\" />\\n  <p class=\\\"pgc-img-caption\\\"></p>\\n </div>\\n <p>因为高血压患者的年龄、病程、血压水平、并发症情况以及对降压药反应和健康状况等均有很大的差异，所以降压药的选用要根据患者的个体状况，药物的作用、代谢、不良反应和药物相互作用，采用个体化给药。</p>\\n <p>在使用降压药的过程中，应该注意以下几点：</p>\\n <p>1、不宜时服时停：有的患者用降压药时服时停，血压一高吃几片，血压一降，马上停药，导致血压反弹。较严重的高血压，可以说是一种终身疾病，应长期坚持服药；</p>\\n <p>2、不宜睡前服药：睡前服药，血压下降，血流变缓慢，血液粘稠度升高，极容易导致血栓形成，引发卒中或心肌梗死。</p>\\n <p>3、不宜降压过快：有些人一旦发现高血压，恨不得立刻就把血压降下来，随意加大用药剂量，这样极容易发生意外；</p>\\n <p>4、不宜频繁换药：降压作用和降压机制不同，选择的降压药物不合适，或者用法、用量不对，降压作用就不明显。因此，高血压患者的用药方案应在医生指导下进行。</p>\\n <div class=\\\"pgc-img\\\">\\n  <img src=\\\"http://p9.pstatp.com/large/pgc-image/1528869679203859cd4fff6\\\" img_width=\\\"1280\\\" img_height=\\\"853\\\" alt=\\\"马来酸依那普利与非洛地平，高血压用药细节要注意！\\\" inline=\\\"0\\\" />\\n  <p class=\\\"pgc-img-caption\\\"></p>\\n </div>\\n <p>如果你喜欢我们的文章，请关注我们的微信号“<strong>好心舒冠心病管家</strong>”，您的关注是我们前进的动力，您的留言是我们改进的目标。关注我们。将会为您提供更多更好的文章。</p>\\n</div>";
        LOGGER.info(html);
        BaseImgCrawlerFilter filter = new BaseImgCrawlerFilter();
        Page page = new Page();
        page.setRawText(html);
        page.setRequest(new Request("https://www.toutiao.com/item/6566383494182732295/"));
        filter.doFilter(page);

    }


    @Test
    public void groupIndexMatcher() {
        String baseUrl = "http://www.techweb.com.cn/internet/2016-02-02/2272978_2.shtml";
        String pStr = "(http://www\\.techweb\\.com\\.cn/\\w+/\\d{4}-\\d{2}-\\d{2}/\\d+_(\\d+).shtml)";
        Pattern pattern = Pattern.compile(pStr, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(baseUrl);
        while (matcher.find()) {
            String index = matcher.group(matcher.groupCount());
            System.out.println(index);
        }

    }

    @Test
    public void downloaderTest() {
        Downloader downloader = new ToutiaoDownloader();
        Task task = new Task() {

            @Override
            public String getUUID() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Site getSite() {
                // TODO Auto-generated method stub
                return new DefaultSite().parse();
            }

        };
        Page page = downloader.download(new Request("https://www.toutiao.com/c/user/6720614628/#mid=6720614628"), task);
        System.out.println(page.getRawText());
    }

    @Test
    public void imageEncodeTest() {
        String html = "<div>\\n <p>小编有一段时间没有给大家说说高血压降压药物了，今天打算借着两种降压药物给大家说一说高血压用药的一些细节。实际上，我们治疗高血压的主要目的是最大限度的降低心血管发病和死亡的总危险。由于心血管病危险性与血压之间的相关呈连续性，因此抗高血压治疗的目标是将血压恢复至正常水平。</p>\\n <p>今天要说的两种降压药物是马来酸依那普利与非洛地平，马来酸依那普利属于血管紧张素转换酶抑制剂，功能主要用于各期原发型高血压、肾性高血压、充血性心力衰竭等的治疗。非洛地平属于二氢砒啶类钙拮抗剂，主要用于轻、中度原发性高血压的治疗。</p>\\n <div class=\\\"pgc-img\\\">\\n  <img src=\\\"http://p9.pstatp.com/large/pgc-image/1528869549146363716b52d\\\" img_width=\\\"323\\\" img_height=\\\"201\\\" alt=\\\"马来酸依那普利与非洛地平，高血压用药细节要注意！\\\" inline=\\\"0\\\" />\\n  <p class=\\\"pgc-img-caption\\\"></p>\\n </div>\\n <p>马来酸依那普利常用剂量为每日10~40mg，分2~3次服。原发型高血压：每次20mg，每日1次。充血性心力衰竭和肾性高血压：每日10~40mg，起始量为每日10mg，剂量视治疗效果调节，一般每次20mg，每日1次。其不良反应较少。少数出现干咳、眩晕、头痛、疲乏、腹泻、皮疹、味觉障碍、均轻微、短暂。罕有神经血管性水肿，如出现应立即停用，并迅速就医。</p>\\n <p>非洛地平起始剂量2.5mg，一日2次，或遵医嘱。常用维持剂量每日为5mg 或10mg，必要时剂量可进一步增加，或加用其它降压药。服药应早晨用水吞服。非洛地平和其它钙拮抗药相同，在某些病人身上会导致面色潮红、头痛、头晕、心悸和疲劳，这些反应大部分具有剂量依赖性，而且是在剂量增加后开始的短时间内出现，是暂时的，应用时间延长后消失。另外非洛地平可引起与剂量有关的踝肿、牙龈或牙周炎，患者用药后可能会引起轻微的牙龈肿大。在极少数病人中可能会引起显著的低血压伴心动过速，这在易感个体可能会引起心肌缺氧。</p>\\n <div class=\\\"pgc-img\\\">\\n  <img src=\\\"http://p1.pstatp.com/large/pgc-image/1528869620791a29f93ee74\\\" img_width=\\\"800\\\" img_height=\\\"800\\\" alt=\\\"马来酸依那普利与非洛地平，高血压用药细节要注意！\\\" inline=\\\"0\\\" />\\n  <p class=\\\"pgc-img-caption\\\"></p>\\n </div>\\n <p>因为高血压患者的年龄、病程、血压水平、并发症情况以及对降压药反应和健康状况等均有很大的差异，所以降压药的选用要根据患者的个体状况，药物的作用、代谢、不良反应和药物相互作用，采用个体化给药。</p>\\n <p>在使用降压药的过程中，应该注意以下几点：</p>\\n <p>1、不宜时服时停：有的患者用降压药时服时停，血压一高吃几片，血压一降，马上停药，导致血压反弹。较严重的高血压，可以说是一种终身疾病，应长期坚持服药；</p>\\n <p>2、不宜睡前服药：睡前服药，血压下降，血流变缓慢，血液粘稠度升高，极容易导致血栓形成，引发卒中或心肌梗死。</p>\\n <p>3、不宜降压过快：有些人一旦发现高血压，恨不得立刻就把血压降下来，随意加大用药剂量，这样极容易发生意外；</p>\\n <p>4、不宜频繁换药：降压作用和降压机制不同，选择的降压药物不合适，或者用法、用量不对，降压作用就不明显。因此，高血压患者的用药方案应在医生指导下进行。</p>\\n <div class=\\\"pgc-img\\\">\\n  <img src=\\\"http://p9.pstatp.com/large/pgc-image/1528869679203859cd4fff6\\\" img_width=\\\"1280\\\" img_height=\\\"853\\\" alt=\\\"马来酸依那普利与非洛地平，高血压用药细节要注意！\\\" inline=\\\"0\\\" />\\n  <p class=\\\"pgc-img-caption\\\"></p>\\n </div>\\n <p>如果你喜欢我们的文章，请关注我们的微信号“<strong>好心舒冠心病管家</strong>”，您的关注是我们前进的动力，您的留言是我们改进的目标。关注我们。将会为您提供更多更好的文章。</p>\\n</div>";
        String reg = "(<img.*src\\\\s*=\\\\s*(.*?)[^>]*?>)";
        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            System.out.println(matcher.start());
        }
//        String resultHtml = HtmlParserUtils.parser(html, TagParserEnum.img_parser_url);
//        LOGGER.info(resultHtml);
    }

    @Test
    public void testForHtmlClean() {
        HtmlTagFilterConverter converter = new HtmlTagFilterConverter();
        String content =
                "<!--文章开始-->  <p class=\"mcePageBreak\">&nbsp;</p>  <p><strong>“纽约留学女”王胜寒创业&nbsp;徐小平做天使</strong></p>  <p style=\"text-align: center; text-indent: 0;\"><img src=\"http://upload.techweb.com.cn/2016/0323/1458704145697.jpeg\" border=\"0\" width=\"450\" alt=\"当今十大网红，颜值嘴炮估值上亿：资本网红教主王思聪，内容鼻祖罗胖、新晋网红papi酱、咪蒙\" title=\"\" height=\"400\" /></p>  <p>难道整容乃网红第一要义？吐槽姐告诉我们，错！人生在世，全靠演技。2012年春节期间，萝莉脸御姐身的王胜寒因为扮演“纽约留学女”，在镜头前吐槽反讽回国后的种种不适应而走红。</p>  <p>2014年王胜寒创立“醉鹅娘”，由徐小平投资做葡萄酒和西餐的互联网入门教育，旨在成为一个优质生活方式的内容提供商与服务供应商。</p>  <!--阿里云相关阅读  2015-09-06 手动删除 -->  <style> .tag-editor {   text-align: right;  font-size: 12px;    color: #717171; }  .tag {   float: left; }  .tag a {    margin-right: 10px;     color: #0B3B8C; } </style>  <!-- end阿里云相关阅读-->  <!-- 文章结束-->  <!--微信二维码随机  -->  <div align=\"center\" style=\"margin-top:15px;\">   <a align=\"center\"> <img alt=\"tw_wechat\" src=\"http://s2.techweb.com.cn/static/images/epchina_wechat.jpg?2015092301\" border=\"0\" /> </a>  </div>  <div class=\"tag-editor\">   <style> .tag-editor {     text-align: right;  font-size: 12px;    color: #717171; }  .tag {   float: left; }  .tag a {    margin-right: 10px;     color: #0B3B8C; } </style>   <span class=\"tag\">标签： <a href=\"http://www.techweb.com.cn/tag/网红/\" target=\"_blank\"> 网红 </a> <a href=\"http://www.techweb.com.cn/tag/王思聪/\" target=\"_blank\"> 王思聪 </a> <a href=\"http://www.techweb.com.cn/tag/papi酱/\" target=\"_blank\"> papi酱 </a> <a href=\"http://www.techweb.com.cn/tag/罗辑思维/\" target=\"_blank\"> 罗辑思维 </a> <a href=\"http://www.techweb.com.cn/tag/咪蒙/\" target=\"_blank\"> 咪蒙 </a> </span>   <span id=\"editor_baidu\" class=\"editor\" style=\"text-align: right; text-indent: 0;\">( 责任编辑:朱迪) </span>  </div>  <!--<wb:like type=\"simple\"></wb:like>-->  <!-- 内容分页 -->  <div class=\"page\">   <div class=\"page\">   <a href=\"http://www.techweb.com.cn/column/2016-03-23/2302051_1.shtml\">上一页</a>   <a href=\"http://www.techweb.com.cn/column/2016-03-23/2302051_1.shtml\">1</a>   <a href=\"http://www.techweb.com.cn/column/2016-03-23/2302051_2.shtml\">2</a>   <a href=\"http://www.techweb.com.cn/column/2016-03-23/2302051_3.shtml\">3</a>   <a href=\"http://www.techweb.com.cn/column/2016-03-23/2302051_4.shtml\">4</a>   <a href=\"http://www.techweb.com.cn/column/2016-03-23/2302051_5.shtml\">5</a>   <a href=\"http://www.techweb.com.cn/column/2016-03-23/2302051_6.shtml\">6</a>   <span class=\"current\">7</span>   <a href=\"http://www.techweb.com.cn/column/2016-03-23/2302051_8.shtml\">8</a>   <a href=\"http://www.techweb.com.cn/column/2016-03-23/2302051_9.shtml\">9</a>   <a href=\"http://www.techweb.com.cn/column/2016-03-23/2302051_10.shtml\">10</a>   <a href=\"http://www.techweb.com.cn/column/2016-03-23/2302051_8.shtml\">下一页</a>  </div>  </div>";
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        List<String> xpathSelectors = new ArrayList<String>();
        xpathSelectors.add("//div[@class='page']");
        xpathSelectors.add("//div[@align='center']");
        xpathSelectors.add("//div[@class='tag-editor']");
        params.put("excludeTag", Arrays.asList("style"));
        params.put("excludeXpath", xpathSelectors);
        String result = (String) converter.converter(null, content, params);
        LOGGER.info(result);
    }
}

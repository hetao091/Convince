package com.agile.convince;

import android.util.Log;

import com.agile.sign.SignaturePad;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.LineSegment;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.itextpdf.text.pdf.parser.Vector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @Description: pdf操作类
 * <p>
 * Created by hetao on 15/7/15 下午2:54.
 */
public class PDFOperateUtil {

    // 定义返回页码
    private static int i = 0;
    private static  final String KEY_WORD = "身故保险金受益人";
    //

    /**
     * @param inBytes  文件字节
     *
     */
    public static byte[] addSignMark(byte[] inBytes) {

        //获取客户签字绝对位置
        List<float[]> coordList = getKeyWords(inBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //
        Log.d("--inBytes---", "" + inBytes.length);
        PdfReader reader = null;
        PdfStamper stamp = null;
        byte[] bytes = null;
        try {
            reader = new PdfReader(inBytes, "PDF".getBytes());
            stamp = new PdfStamper(reader, outputStream);
            for (int m = 0; m < coordList.size(); m++) {
                float[] resu = coordList.get(m);
                Log.v("resu","resu");
                int page = (int) resu[2];
                PdfContentByte under = stamp.getOverContent(page);
                under.setLineWidth(3.0f);
                under.setColorStroke(BaseColor.BLACK);
                under.setLineCap(PdfContentByte.LINE_CAP_ROUND);
                under.setLineJoin(PdfContentByte.LINE_CAP_ROUND);

                float pdfX = reader.getPageSize(page).getWidth();
                float pdfY = reader.getPageSize(page).getHeight();
                float padWidth = DrawActivity.padWidth;//手写板宽度
                float padHeight = DrawActivity.padHeight;//手写板宽度
                //
                float mScale = padWidth / padHeight;
                float signX = resu[0];//客户签字X坐标
                float signY = resu[1];//客户签字Y坐标
                float bX = 0;//记录手指滑动x轴坐标
                float bY = 0;//记录手指移动Y轴坐标
                //总共的笔画数
                for (int k = 0; k < SignaturePad.mListAllX.size(); k++) {
                    Log.v("------","------");
                    float beginX = 0;//记录贝塞尔控制点坐标X
                    float beginY = 0;//记录贝塞尔控制点坐标Y
                    ArrayList<Float> listX = SignaturePad.mListAllX.get(k);
                    ArrayList<Float> listY = SignaturePad.mListAllY.get(k);
                    //每一笔的路径坐标
                    for (int g = 0; g < listX.size(); g++) {
                        bX = listX.get(g);
                        bY = listY.get(g);

                        /*********横竖屏坐标换算************/
                        if (padHeight < padWidth) {
                            bX = signX + 50 + ((bX / (padWidth / pdfX)) / 3f);
                            bY = signY - 80 + (((pdfY - (bY / (padHeight / pdfY))) / mScale) / (3.5f));
                        } else {
                            bX = signX + 80 + ((bX / (padWidth / pdfX)) / 5.5f);
                            bY = signY - 100 + (((pdfY - (bY / (padHeight / pdfY)))) / 5.5f);
                        }
                        Log.v("-----","bX:"+bX+"--bY"+bY);
                        //
                        /*****开始*******/
                        if (g == 0) {
                            beginX = bX;
                            beginY = bY;
                            under.moveTo(bX, bY);
                        }
                        /****滑动*****/
                        //贝塞尔处理
                        under.curveTo(beginX, beginY, (beginX + bX) / 2, (beginY + bY) / 2);
                        beginX = bX;
                        beginY = bY;
                        /*****结束****/
                        if (g == listX.size()) {
                            under.lineTo(bX, bY);
                        }
                    }
                    /***置空标记**/
                }

                under.stroke();
                under.closePathStroke();
            }
            stamp.close();
            bytes = outputStream.toByteArray();
            Log.d("--bytes---", "" + bytes.length);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * @param mBytes 文件
     *               关键字
     * @return 坐标数据
     */
    private static List<float[]> getKeyWords(byte[] mBytes) {

        MyRenderListener listener = new MyRenderListener(KEY_WORD);

        try {
            PdfReader pdfReader = new PdfReader(mBytes);
            int pageNum = pdfReader.getNumberOfPages();
            final float[] resu = new float[3];
            PdfReaderContentParser pdfReaderContentParser = new PdfReaderContentParser(
                    pdfReader);
            // 下标从1开始
            for (i = 1; i <= pageNum; i++) {
                pdfReaderContentParser.processContent(i, listener);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<float[]> mList = listener.list;
        return mList;
    }


    private static class MyRenderListener implements RenderListener {
        public List<float[]> list = new ArrayList<>();

        private Vector lastStart;
        private Vector lastEnd;
        private String KEY_WORD;
        private StringBuffer result = new StringBuffer();

        public MyRenderListener() {
        }

        MyRenderListener(String KEY_WORD) {
            this.KEY_WORD = KEY_WORD;
        }

        public String getResultantText() {
            return this.result.toString();
        }

        final void appendTextChunk(CharSequence text) {
            this.result.append(text);
        }

        @Override
        public void renderText(TextRenderInfo renderInfo) {
            boolean firstRender = this.result.length() == 0;
            boolean hardReturn = false;
            LineSegment segment = renderInfo.getBaseline();
            Vector start = segment.getStartPoint();
            Vector end = segment.getEndPoint();
            if (!firstRender) {
                Vector x1 = this.lastStart;
                Vector x2 = this.lastEnd;
                float dist = x2.subtract(x1).cross(x1.subtract(start)).lengthSquared() / x2.subtract(x1).lengthSquared();
                float sameLineThreshold = 1.0F;
                if (dist > sameLineThreshold) {
                    hardReturn = true;
                }
            }

            if (hardReturn) {
                this.appendTextChunk("\n");
            } else if (!firstRender && this.result.charAt(this.result.length() - 1) != 32 && renderInfo.getText().length() > 0 && renderInfo.getText().charAt(0) != 32) {
                float spacing = this.lastEnd.subtract(start).length();
                if (spacing > renderInfo.getSingleSpaceWidth() / 2.0F) {
                    this.appendTextChunk(" ");
                }
            }

            this.appendTextChunk(renderInfo.getText());
            this.lastStart = start;
            this.lastEnd = end;
            LineSegment baseline = renderInfo.getBaseline();
            float x = baseline.getStartPoint().get(0);
            float y = baseline.getStartPoint().get(1);
            float w = baseline.getLength();

            if (null != this.result.toString() && this.result.toString().contains(KEY_WORD)) {
                float[] resu = new float[3];
                resu[0] = x;
                resu[1] = y;
                resu[2] = i;
                list.add(resu);
                // 置空
                result = new StringBuffer();
            }

        }


        @Override
        public void beginTextBlock() {

        }


        @Override
        public void endTextBlock() {

        }

        @Override
        public void renderImage(ImageRenderInfo imageRenderInfo) {

        }
    }


}

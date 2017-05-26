package com.agile.pdf;

import android.content.Context;
import android.content.res.AssetManager;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.PrivateKeySignature;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;

/**
 * 创建人:    何 涛
 * 创建时间:  2017/5/25 下午1:43
 * 描述:
 */

public class AddSignature {
    private static final String KEY = "qx_demo.pfx";
    private static final char[] PASSWORD = "123456".toCharArray();
    private static final String IMG = "demo.gif";

    private Context mContext;

    public AddSignature(Context context) {
        mContext = context;
    }

    private byte[] sign(byte[] src,
                      Certificate[] chain, PrivateKey pk, String digestAlgorithm, String provider, MakeSignature.CryptoStandard subfilter, String reason, String location)
            throws GeneralSecurityException, IOException, DocumentException {
        // Creating the reader and the stamper
        PdfReader reader = new PdfReader(src);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        //创建签章工具PdfStamper ，最后一个boolean参数
        //false的话，pdf文件只允许被签名一次，多次签名，最后一次有效
        //true的话，pdf可以被追加签名，验签工具可以识别出每次签名之后文档是否被修改
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0', null, true);
        // Creating the appearance
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);

        Image image = Image.getInstance(getAssetsBytes(IMG));
//        image.scaleAbsolute(126f, 126f);
//        image.setAbsolutePosition(270, 110);
        appearance.setSignatureGraphic(image);
        appearance.setAcro6Layers(true);
        appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);
        appearance.setVisibleSignature(new Rectangle(320, 150, 446, 276), 1, "sig");
        ExternalDigest digest = new BouncyCastleDigest();
        ExternalSignature signature =
                new PrivateKeySignature(pk, digestAlgorithm, provider);
        MakeSignature.signDetached(appearance, digest, signature, chain,
                null, null, null, 0, subfilter);
        //
       return os.toByteArray();

    }

    public byte[] addSignture(byte[] mBytes) throws GeneralSecurityException, IOException, DocumentException {

        AssetManager assetManager = mContext.getAssets();
        //assets目录下放pkcs12证书文件
        InputStream inputStream = assetManager.open(KEY);
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);

        KeyStore ks = KeyStore.getInstance("PKCS12");

        ks.load(inputStream, PASSWORD);
        String alias = (String) ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, PASSWORD);
        Certificate[] chain = ks.getCertificateChain(alias);

        //


//        sign(SRC, "Signature1", String.format(DEST, 1), chain, pk,
//                DigestAlgorithms.SHA256, provider.getName(), MakeSignature.CryptoStandard.CMS,
//                "Appearance 1", "Ghent", PdfSignatureAppearance.RenderingMode.DESCRIPTION, null);
//        sign(SRC, "Signature1", String.format(DEST, 2), chain, pk,
//                DigestAlgorithms.SHA256, provider.getName(), MakeSignature.CryptoStandard.CMS,
//                "Appearance 2", "Ghent", PdfSignatureAppearance.RenderingMode.NAME_AND_DESCRIPTION, null);
       return sign(mBytes, chain, pk, DigestAlgorithms.SHA256, provider.getName(), MakeSignature.CryptoStandard.CMS, "山东确信签章演示", "测试");
//
//        sign(SRC, "Signature1", String.format(DEST, 4), chain, pk,
//                DigestAlgorithms.SHA256, provider.getName(), MakeSignature.CryptoStandard.CMS,
//                "Appearance 4", "Ghent", PdfSignatureAppearance.RenderingMode.GRAPHIC, image);
    }

    private byte[] getAssetsBytes(String fileName) {
        byte[] buffer = null;
        try {
            InputStream is = mContext.getAssets().open(fileName);
            int lenght = is.available();
            buffer = new byte[lenght];
            is.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer;
    }
}

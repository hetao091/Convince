package com.agile.pdf;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.security.CertificateInfo;
import com.itextpdf.text.pdf.security.PdfPKCS7;
import com.itextpdf.text.pdf.security.SignaturePermissions;

import org.bouncycastle.tsp.TimeStampToken;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建人:    何 涛
 * 创建时间:  2017/5/25 下午5:32
 * 描述:
 */

public class Inspect extends InspectPDF {
    private SignaturePermissions inspectSignature(AcroFields fields, String name, SignaturePermissions perms, StringBuilder mList) throws GeneralSecurityException, IOException {
        List<AcroFields.FieldPosition> fps = fields.getFieldPositions(name);
        if (fps != null && fps.size() > 0) {
            AcroFields.FieldPosition fp = fps.get(0);
            Rectangle pos = fp.position;
            if (pos.getWidth() == 0 || pos.getHeight() == 0) {
                System.out.println("签名域不可见\n");
                mList.append("签名域不可见\n");
            } else {
                System.out.println(String.format("Field on page %s; llx: %s, lly: %s, urx: %s; ury: %s",
                        fp.page, pos.getLeft(), pos.getBottom(), pos.getRight(), pos.getTop()));

            }
        }

        PdfPKCS7 pkcs7 = super.verifySignature(fields, name);
        if (pkcs7 == null) {
            return null;
        }
        System.out.println("Digest algorithm 摘要算法 : " + pkcs7.getHashAlgorithm());
        mList.append("\n摘要算法 : " + pkcs7.getHashAlgorithm());
        System.out.println("Encryption algorithm 加密算法 : " + pkcs7.getEncryptionAlgorithm());
        mList.append("\n加密算法 : ").append(pkcs7.getEncryptionAlgorithm());
        System.out.println("Filter subtype 过滤器类型 : " + pkcs7.getFilterSubtype());
        mList.append("\n过滤器类型 : ").append(pkcs7.getFilterSubtype());

        X509Certificate cert = (X509Certificate) pkcs7.getSigningCertificate();

        System.out.println("公钥: " + cert.getPublicKey());
        mList.append("\n公钥: " + cert.getPublicKey());

        System.out.println("Issuer 发布者: " + cert.getIssuerDN());
        mList.append("\n发布者: " + cert.getIssuerDN());

        System.out.println("Subject: " + cert.getSubjectDN());


        System.out.println("Name of the signer 签名者姓名: " + CertificateInfo.getSubjectFields(cert).getField("CN"));

        mList.append("\n签名者姓名: " + CertificateInfo.getSubjectFields(cert).getField("CN"));

        if (pkcs7.getSignName() != null)
            System.out.println("Alternative name of the signer 其他签名者姓名: " + pkcs7.getSignName());
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");

        System.out.println("Signed on 签约时间: " + date_format.format(pkcs7.getSignDate().getTime()));
        mList.append("\n签约时间: " + date_format.format(pkcs7.getSignDate().getTime()));

        System.out.println("Valid from: " + date_format.format(cert.getNotBefore()));
        System.out.println("Valid to: " + date_format.format(cert.getNotAfter()));

        mList.append("\n证书有效期: " + date_format.format(cert.getNotBefore()) + "-" + date_format.format(cert.getNotAfter()));
        try {
            cert.checkValidity(pkcs7.getSignDate().getTime());
            System.out
                    .println("The certificate was valid at the time of signing. 文档在证书在有效期内签署");
            mList.append("文档在证书在有效期内签署 ");
        } catch (CertificateExpiredException e) {
            System.out
                    .println("The certificate was expired at the time of signing.文档在证书在有效期外签署");

            mList.append("\n文档在证书在有效期外签署 ");

        } catch (CertificateNotYetValidException e) {
            System.out
                    .println("The certificate wasn't valid yet at the time of signing. ");
        }
        try {
            cert.checkValidity();
            System.out.println("The certificate is still valid. 证书有效性正常");
            mList.append("\n证书有效性正常 ");
        } catch (CertificateExpiredException e) {
            System.out.println("The certificate has expired. 证书已过期");
            mList.append("\n证书已过期 ");
        } catch (CertificateNotYetValidException e) {
            System.out.println("The certificate isn't valid yet. 证书无效");
            mList.append("\n证书无效 ");
        }


        if (pkcs7.getTimeStampDate() != null) {
            System.out.println("TimeStamp 时间戳: " + date_format.format(pkcs7.getTimeStampDate().getTime()));
            mList.append("\n时间戳: " + date_format.format(pkcs7.getTimeStampDate().getTime()));
            TimeStampToken ts = pkcs7.getTimeStampToken();

            System.out.println("TimeStamp service 时间戳服务器: " + ts.getTimeStampInfo().getTsa());
            mList.append("\n时间戳服务器: " + ts.getTimeStampInfo().getTsa());

            System.out.println("Timestamp verified 时间戳验证? " + pkcs7.verifyTimestampImprint());
            mList.append("\n时间戳验证: " + pkcs7.verifyTimestampImprint());
        }

        System.out.println("Location : " + pkcs7.getLocation());
        mList.append("\n定位: " + pkcs7.getLocation());

        System.out.println("Reason 原因: " + pkcs7.getReason());
        mList.append("\n因素: " + pkcs7.getReason());

        PdfDictionary sigDict = fields.getSignatureDictionary(name);
        PdfString contact = sigDict.getAsString(PdfName.CONTACTINFO);
        if (contact != null) {
            System.out.println("Contact info 联系信息: " + contact);
            mList.append("\n联系信息: " + contact);
        }

        perms = new SignaturePermissions(sigDict, perms);
        System.out.println("Signature type 签名类型 : " + (perms.isCertification() ? "certification" : "approval"));

        mList.append("\n签名类型 : " + (perms.isCertification() ? "certification" : "approval"));

        System.out.println("Filling out fields allowed: 允许字段填充" + perms.isFillInAllowed());
        mList.append("\n允许字段填充  " + perms.isFillInAllowed());

        System.out.println("Adding annotations allowed: 允许添加注释" + perms.isAnnotationsAllowed());
        mList.append("\n允许添加注释  " + perms.isAnnotationsAllowed());

        for (SignaturePermissions.FieldLock lock : perms.getFieldLocks()) {
            System.out.println("Lock: 锁" + lock.toString());
        }
        return perms;
    }

    public int inspectSignatures(byte[] mBytes, StringBuilder mList) throws IOException, GeneralSecurityException {
        PdfReader reader = new PdfReader(mBytes);
        AcroFields fields = reader.getAcroFields();
        ArrayList<String> names = fields.getSignatureNames();
        SignaturePermissions perms = null;
        for (String name : names) {
            System.out.println("===== " + name + " =====");
            perms = inspectSignature(fields, name, perms, mList);
        }
        System.out.println();
        return names.size();
    }

}

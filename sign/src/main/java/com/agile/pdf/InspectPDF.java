package com.agile.pdf;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.security.PdfPKCS7;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

/**
 * 创建人:    何 涛
 * 创建时间:  2017/5/25 下午1:42
 * 描述:
 */

class InspectPDF {

    PdfPKCS7 verifySignature(AcroFields fields, String name) throws GeneralSecurityException, IOException{
        System.out.println("整个文件进行数字签名: " + fields.signatureCoversWholeDocument(name));
        System.out.println("签名版本: " + fields.getRevision(name) + " of " + fields.getTotalRevisions());
        PdfPKCS7 pkcs7 = null;
        try {
            pkcs7 = fields.verifySignature(name);
            System.out.println("文件完整性检查? " + pkcs7.verify());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pkcs7;
    }

    public  void verifySignatures(byte[] mBytes) throws IOException, GeneralSecurityException {
        PdfReader reader = new PdfReader(mBytes);
        AcroFields fields = reader.getAcroFields();
        ArrayList<String> names = fields.getSignatureNames();
        for (String name : names) {
            System.out.println("===== " + name + " =====");
            verifySignature(fields, name);
        }
        System.out.println();
    }


}

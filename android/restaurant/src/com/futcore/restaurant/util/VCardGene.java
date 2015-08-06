package com.futcore.restaurant.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import net.sourceforge.cardme.io.VCardWriter;
import net.sourceforge.cardme.vcard.VCard;
import net.sourceforge.cardme.vcard.VCardImpl;
import net.sourceforge.cardme.vcard.arch.LanguageType;
import net.sourceforge.cardme.vcard.arch.ParameterTypeStyle;
import net.sourceforge.cardme.vcard.arch.VCardTypeName;
import net.sourceforge.cardme.vcard.arch.VCardVersion;
import net.sourceforge.cardme.vcard.exceptions.VCardBuildException;
import net.sourceforge.cardme.vcard.types.AdrType;
import net.sourceforge.cardme.vcard.types.EmailType;
import net.sourceforge.cardme.vcard.types.FNType;
import net.sourceforge.cardme.vcard.types.NameType;
import net.sourceforge.cardme.vcard.types.NoteType;
import net.sourceforge.cardme.vcard.types.OrgType;
import net.sourceforge.cardme.vcard.types.TelType;
import net.sourceforge.cardme.vcard.types.TitleType;
import net.sourceforge.cardme.vcard.types.UrlType;
import net.sourceforge.cardme.vcard.types.VersionType;
import net.sourceforge.cardme.vcard.types.params.AdrParamType;
import net.sourceforge.cardme.vcard.types.params.ExtendedParamType;
import net.sourceforge.cardme.vcard.types.params.TelParamType;

import com.futcore.restaurant.models.VCardInfo;


public class VCardGene
{
    VCardInfo mVCardInfo;
    String mVCardString = "";
    
    public VCardGene(VCardInfo vCardInfo)
    {
        mVCardInfo = vCardInfo;
        generate();
    }

    public void generate()
    {
		VCard vcard = new VCardImpl();
		vcard.setVersion(new VersionType(VCardVersion.V3_0));
		vcard.setName(new NameType("Futcore Card"));
        
        //        vcard.addEmail(new EmailType("yanjun.zhou@futcore.com"));

        /*		NType n = new NType();
		n.setEncodingType(EncodingType.QUOTED_PRINTABLE);
		n.setCharset(Charset.forName("UTF-8"));
		n.setLanguage(LanguageType.EN);
		n.setFamilyName("周");
		n.setGivenName("彦俊");
        //		n.addHonorificPrefix("Mr.");
        //		n.addHonorificSuffix("I");
        //		n.addAdditionalName("Johny");
		vcard.setN(n);
        */

        FNType fn = new FNType();
        fn.setCharset("UTF-8");
        fn.setFormattedName(mVCardInfo.fName);
        fn.setCharset(Charset.forName("UTF-8"));
        fn.setLanguage(LanguageType.ZH_CN);
        vcard.setFN(fn);

		OrgType organizations = new OrgType();
		organizations.setOrgName(mVCardInfo.organization);
        //		organizations.addOrgUnit("技术部");
		vcard.setOrg(organizations);


		TelType telephone = new TelType();
		telephone.setCharset("UTF-8");
		telephone.setTelephone(mVCardInfo.telephone);
		telephone.addParam(TelParamType.CELL)
		.addParam(TelParamType.HOME)
		.setParameterTypeStyle(ParameterTypeStyle.PARAMETER_VALUE_LIST);
		vcard.addTel(telephone);

		TelType telephone2 = new TelType();
		telephone2.setTelephone(mVCardInfo.telephone2);
		telephone2.addParam(TelParamType.FAX)
		.addParam(TelParamType.WORK)
		.setParameterTypeStyle(ParameterTypeStyle.PARAMETER_LIST);
		vcard.addTel(telephone2);


		AdrType address1 = new AdrType();
		address1.setCharset("UTF-8");
		address1.setExtendedAddress("");
		address1.setCountryName(mVCardInfo.country);
		address1.setLocality(mVCardInfo.locality);
		address1.setRegion(mVCardInfo.region);
		address1.setPostalCode(mVCardInfo.postalCode);
        //		address1.setPostOfficeBox("25334");
		address1.setStreetAddress(mVCardInfo.streetAddress);
		address1.addParam(AdrParamType.HOME)
		.addParam(AdrParamType.PARCEL)
		.addParam(AdrParamType.PREF)
		.addExtendedParam(new ExtendedParamType("CUSTOM-PARAM-TYPE", VCardTypeName.ADR))
		.addExtendedParam(new ExtendedParamType("CUSTOM-PARAM-TYPE", "WITH-CUSTOM-VALUE", VCardTypeName.ADR));
        
        vcard.addEmail(new EmailType(mVCardInfo.email));

        vcard.setTitle(new TitleType(mVCardInfo.title));

		try {
			vcard.addUrl(new UrlType(new URL(mVCardInfo.url)));
		} catch (NullPointerException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (MalformedURLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        
        //        vcard.setGeo(new GeoType(3.4f, -2.6f));

        NoteType note = new NoteType();
        note.setNote(mVCardInfo.note);
        vcard.addNote(note);
        
		VCardWriter writer = new VCardWriter();

        //		writer.setOutputVersion(VCardVersion.V3_0);
        //		writer.setFoldingScheme(FoldingScheme.MIME_DIR);
//		writer.setCompatibilityMode(CompatibilityMode.RFC2426);
//		writer.setBinaryfoldingScheme(BinaryFoldingScheme.MIME_DIR);
        
        writer.setVCard(vcard);
        //        System.out.println(writer.getVCard()+"iiiiiiiiiiiiiiiiiiiiiiiiiiiii9999999999999999999999999999");
        
		try {
			mVCardString = writer.buildVCardString();
		} catch (VCardBuildException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        try {
			mVCardString = new String(mVCardString.getBytes("UTF-8"),"ISO-8859-1");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//如果不想更改源码，则将字符串转换成ISO-8859-1编码        
    }

    public String getStr()
    {
        return mVCardString;
    }
}

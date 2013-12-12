/***************************************************************************
    begin........: February 2012
    copyright....: Sebastian Fedrau
    email........: lord-kefir@arcor.de
 ***************************************************************************/

/***************************************************************************
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    General Public License for more details.
 ***************************************************************************/
package accounting;

public final class AppInfo
{
	public static String TITLE = "Bookkeeping";
	public static String DATE = "2012-2013";
	public static String URL = "http://github.com/20centaurifux/Bookkeeping";
	public static int VERSION_MAJOR = 0;
	public static int VERSION_MINOR = 1;
	public static int VERSION_PATCHLEVEL = 1;
	public static String AUTHOR = "Sebastian Fedrau";
	public static String AUTHOR_EMAIL = "sebastian.fedrau@gmail.com";

	public static String[] ICON =
	{
		" :: :,,:::::::::::::::::::::::::,::,,,,::::  iiiii1tfffffftftfffLLLffffLLfffLLfLLffffLLLLCC80ttCttLLLLLfffft1111111ti:,tGGCfCG",
		"::::::::::::::::::::::::,::,:,:itfLCGGGG0000000G0GGCLLfffLLLLLLLttttfftttfLftfLLLfftffffft1fL08Lt1fLLttt1tt111tttffff ,i0GCLCG",
		":::::::::::::::::::,,,,,::,:1CGGGGCCCCCG00GGGGGGGGG00G080GGCLfttttfLftttt11tffffLLLftttt111ii1itffLLLtt1:1t1CL1ti1tff . GGGGGG",
		":,,,,:::::::::,,,,,,,,,,,,tGGG000000000080000000000000GCGCCCG0000Ct11tfttt1111ttffLLLftt1ti1ttffLLffLttf:1t1GL1tiifttLCCG0GGGG",
		":,,,,::,,,,,,,,,,,:,,,,,iCGGGG0G0G0000000000000000000000000000GGCLLCGCti1tffftt11ttfLCCLLLfLLLLfCGLfLttti1ttLC1t1 tffLG00000GG",
		"::,,,::,,,,,,,,,,,,,,,,iG0GG0G0G00008800GGGGGGGG000000000000880000GGCLfLLL1itfftttttffLLLCCLLCGLC8CLLLLffLfffffffffftfGG000000",
		"::,,,,,,,,,,,,,,,,,,,, GGGG0G008888880GGCCCCCCCCCCCGGGGG0000008808008GCCLftfCf1ttttttttfffLLLLCCL8CfLftt1itti11it11tttL00GGG00",
		",,,,,,,,,,,,,,,,,,,,,,fGGG0008888888GCLLLLLfLLLLLLCCCCCGGGGG00000888880808GLffffLft111tftftfLfLCCG0tft1ti t11GL11iitt1i: i1fLG",
		",,,,,,,,,,,,,,,,,,,,,,CGG0088888888CLffttttffffffLLLLLCCCCCCGGGG0000800888800GCLftLGCf111ttffffLLLGC1ftt1:ttiCC1ii tttfffftt11",
		",,,,,,,,,,,,,,,,,,,.,,tG0000008888LtttttttttfffffffffLLLLLLCCCGGGGG0000888888888GLCLtL08Lft1ttftfLtfff111:1t1tLi 1 1fLffffLffL",
		",,,,,,,,,,,,,,,,,.,.,.:C000888888C11111tttttttttffffffLLLLLLCCCCGGGGG0000088888888@8CCGtC88LfftfftftfLLfLffLLLLftttttfffLL1itt",
		",,...,,,,,,,,,,...,....iG00088888ti111tttttttttfffffffffLLLLLLCCCCGGGGG0000000888@8888000LL88CLftfftffii1i1t1itii111111tf tfLf",
		".,.,,.,,..,,,......     t0088088G1i111t11tttttffffffLLffLLLLLLLCCCCCCCGGG0000000888888888GGGLG80LLttLf1i  itttLL11  1t1tf tfff",
		",,,,,,,,.,.............. f008888fiii111111ttffLLLffLLLLLLLLLLLLCLLCCCCCGG000000000888888888800GLG8Cffft11  111CGiii:1t11f 1tft",
		" 1.,.:,.................. f0008Liiiiiii1tfLCGGGGGCCCCCCLLLLLCCLLLLLCCCGGG000088888@8@80G8888@8080LG0Lt1ii :1t1f0ii1:it11f  1ti",
		"i,:t,.,, ,..,..............108G1 iii1tfLCG000GCLLLLLLLLLCLLLLLLCCCCCCGGGGG000000G000GLLGCG08808@000CCGfttftfftt111t1t111t1:111",
		"i  i.,i11 1,..,,,,......... :GGii 1iitCLCCtitCCCCGCLLLfLfffLLfLLLLLLLLLLCCCGG000GGGCLLC0GLLG08888880GLCf1i1t11ift111t111t1,tit",
		"f11i1i i ,i.:11  ,........... it11i1CCLfCGGGffffLt11fLLCfffffffffLLLLLLLLLCCCCGGCGCLLLCGCLLLC08888880GCLfi1tt tfL1itft1111,t11",
		"i1ftftt111ii  ,.i. .. .... ..  GGL1LCC0CLCCGG1t080GCCCCCLLfffffffffLLLLLLLLCCCCCGCCCCLCGGCLLLLG00888880CLLi1ti1CC 1itt1ttt:iii",
		",,,  ii11ffftt11i ,..  . ,1t1ttiittLCCLG0GCCGL LGCCLLLLLLLffffffffffffffffLLLCCCCCCCCCCGGGCLLLCGC0888880GLftf11LL i1tft1fCt iL",
		" .tf .,,,:ii111fftt111i,ifffffff1ffLLLLCCGGGGLfCCCCLLLfffffffffffffffffffffLLLLCCCCCLLCGGGCCCLCCC@@888800GLLfffffLfLLLLLtLGLfG",
		" ,tt  . .t1 .,: 1i11,.:.ifffffftffffC0GCLCCGCtLCCCLLLLfffffffttffffffffffffLLLLCCLLLfLGGCCGCLLCC0@@@@88888Cft111t1t1tttfCCGffC",
		" :11    ,t1  . t1.:,  ,. LftffttfffffLG00GGf1LCCCLLLffffftttttttfffffffffffLLLLLLLLLLG0GLLLLLCLC8@@@@888880LiffitttCt1ttfCGCfL",
		"  ..    ,i .  .L1   .....1ttft1ttffffLLffffLCLLLLfffftttttttttttttffttfffffffLLLLfLfffftffLCCLL0@@@@@@88888Lf0t111f0ffttfCGCLL",
		"  .,     .,.  :t   .tt1t111f11ttfffffLLLLLfffffffftttttttttttttttttttttffffffLLLLfffLfffftfLLLG088@@@@88088LLtfii1:it 1fCCGGCC",
		":,,:,  ..:::  .,.. ,t1t11tt1itttffffffLLLLLffttttttttttttttttttttttttttftfffffLLfffLLfftffffLLCG08@@@@88808LLfffCfttttfCCCGGCC",
		".if,   . : :,:111: :ft111t1iittffttffffffLLLftttttttttttttttttttftttttttffffffffffLLCCLLLCLffLCCG08@@@8880CfCLCG080ffLLCCG0ffL",
		".if,   . t:: ,:t. . 111ttft1ii1ftfftttttLLLffttt1tttttttttttttttttttffffffffffffffLLLLLLfftfLfLLCG0@@@800CGfLL00G088CLCLLLCCLi",
		".1f...  1t,:i.ii : it1i fCft1i: 111ftfLLLffffttttttttttttttttttttttffftftfffffffffffffffffftttffLLC8@800GG0Gt,:1tC88Gtii: 1i 1",
		", i,,:.,1f1 1, 1   ii11i1ttttt:. ,,1tLLLLLfffffftttttttttttttttttttfffffffffffftfffLLfffftttttffffLC00LtfGLt fLfCG0GCLLGCGCLt1",
		"tt1ii11i: : t 1 :: ittt1tt1i11,.::tLLLCCCCCCLLLLCLfffttttttttttttttfffffffffffffffffffffttttttffffffftttfttiif1 : tLCLtL0G0GLf",
		": i  ti   ii11ii iittt1i1tt111 : ,itttfffffffffLLLCLftttttttttttfttfffffLfffffffffffffftttttttttfttt1iCGG0GGCf1  itfLCf1fLfi,.",
		"tLCf: :,,iiiftti   .1tftft11t1. , ,: 1tfffLLLCCCLLLfttttttftttfftttfffffffffffffLLLfffftttttttttttt1. ,LLLfffft1tLCLLLLLf: . .",
		"i,1C1:.:LL,:GffCi f1t .:ftffffi iii iifLfLLLLfffffttttttttttfffftffffffLfLLLfffLfffffffttttttttttti    ,LfLLLLLfL1tCLLCfCCCLi.",
		",.tL::ii 1ftC 11i1t: i1 iitfit: .,i: 1tfffffftttttttfffttfffffffffffffffLLLLLLLLffffffttttttttt11  .    :LLLfLCLLft11LLLLLCGCf",
		"1it1 ii1t:, Lttt , ,, 1tt1tffLtLiiLCC 1tttttttttttttffffffffffffffffLLLLLLLLLLLffffffffttttt1tti, . .    .fLLLtLCCGL11ffLLfi,t",
		"iif111tttti10GGt .. ,Lttt1f1f00CfLGGC1itfffffttttttffffLfffffffffffLLLLCCCCLLLLLfffffftttttttt .   .     . :LCLLLLLCCCCCLCCf  ",
		"1tfti1fCLC fGGGL  : iLGCLt: iGCLLLC0CfttfLLffttttttffffffffLLLLLLLLLCCCCCCLLLLLffffffffttttt1:..  ...        ,1fCLfLLfLCCLCL1:",
		":CCLL: ,,Gf:L0Gf itL00001 tCL1tG: L0GfLLLLLfffffttffffLLLLLLLLLCCCCCCCCCLCLLLLffffffffffttf1:...  ...        ,i11fLLffftLGCCLt",
		"fCCLC1 LftLti, :ff1LG000G0CLitCti1L80CLLLLLffLLLffffffLLLLCCCCCCCCCCCLLCCCLLLLfffffffffftf1 ,,........     :111111t1tfLftfCG0G",
		"LCLtti  11i1tt  CLGGG0GGCCtt:.ti1it0GCLCLCLLLLLLLLLLLLLCCCCCCCCCCCCCCCCLLLLLLfffffftfffffti :,......,. . it111tttt11111fLtfGCC",
		"CCLL111tL.1C11 itGCtCGGLft.. ,1t tG00LLLLCCCLCCCCLLCLLCCLLCCCCCLLCLCCLLLLLLLfffffffffffffti ::,...,,,..ifLfttttttii1fft11fffGG",
		"tCGLftLt ifftfLLCCL1GGCLtf:,,:1 iiCCtLLLLCLLC1i  LLCCCCGCGGGCLLLLLLLLLLLLLLfffftttttttfffft1 :,,,,,::iCGGCCLLLtt1tft1tttttttff",
		"fGitt::, i i1ii:tGL:fGCLti,,,,,,,,Li.t ,tfLCt, .,fLCCCCGGGGG00GCfttfffffffffftttttttttffffft1 :::::iLGCGGCLLfttt1tfftttttttt1t",
		"tCii i,.. . .:.:GGt  Lif: .  . ..   .. .. ,iii,.,ffCCGGGGGG0G00C11t11ttffftttttttttttttttffft1i   L80G0GGCLLGffftffffttttttttt",
		"i1tt  iii1i11ii1LLiiiLL1,. ...: i111111iiiLGGG0GfCLLCCGGGGGGG0Gfi1it11tttttttttttttttttttffffft1L08080G0CCCGCGLfffLLfftttttttt",
		"i1:,,. . ,  :   ii1t11ti . ... ,ti:i1fffffCCLfftfffCCCGGGGGGGGCtii1 tCft1tttttttttttttttffffffC08888880CCG00CCGLffLCLffffffftt"
	};
}
package uw.codegen.jsonxml.codegen;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.util.List;
import java.util.ArrayList;

/**
 * This class is auto generated by tools.
 * @author liliang 
 * @since 2017-12-15 
 */
@JacksonXmlRootElement(localName = "GDSPublisherRequest")
public class GDSPublisherRequest {

    /**
     * 
     */
    private FIDELIO_RateUpdateNotifRQ FIDELIO_RateUpdateNotifRQ = new FIDELIO_RateUpdateNotifRQ();

    public void setFIDELIO_RateUpdateNotifRQ(FIDELIO_RateUpdateNotifRQ FIDELIO_RateUpdateNotifRQ) {
        this.FIDELIO_RateUpdateNotifRQ = FIDELIO_RateUpdateNotifRQ;
    }

    @JacksonXmlProperty(localName="FIDELIO_RateUpdateNotifRQ")
    public FIDELIO_RateUpdateNotifRQ getFIDELIO_RateUpdateNotifRQ (){
        return this.FIDELIO_RateUpdateNotifRQ;
    }


    @JacksonXmlRootElement(localName = "TimeSpan")
    public static class TimeSpan {
        /**
         * 30
         */
        private String duration;

        /**
         * 2012-06-25T00:00:00
         */
        private String start;

        /**
         * 2012-07-25T00:00:00
         */
        private String end;

        public void setDuration(String duration) {
            this.duration = duration;
        }

        @JacksonXmlProperty(localName="duration",isAttribute = true)
        public String getDuration (){
            return this.duration;
        }

        public void setStart(String start) {
            this.start = start;
        }

        @JacksonXmlProperty(localName="start",isAttribute = true)
        public String getStart (){
            return this.start;
        }

        public void setEnd(String end) {
            this.end = end;
        }

        @JacksonXmlProperty(localName="end",isAttribute = true)
        public String getEnd (){
            return this.end;
        }

    }

    @JacksonXmlRootElement(localName = "DOW")
    public static class DOW {
        /**
         * true
         */
        private String Thu;

        /**
         * true
         */
        private String Tue;

        /**
         * true
         */
        private String Wed;

        /**
         * false
         */
        private String Sat;

        /**
         * false
         */
        private String Fri;

        /**
         * true
         */
        private String Mon;

        /**
         * true
         */
        private String Sun;

        public void setThu(String Thu) {
            this.Thu = Thu;
        }

        @JacksonXmlProperty(localName="Thu",isAttribute = true)
        public String getThu (){
            return this.Thu;
        }

        public void setTue(String Tue) {
            this.Tue = Tue;
        }

        @JacksonXmlProperty(localName="Tue",isAttribute = true)
        public String getTue (){
            return this.Tue;
        }

        public void setWed(String Wed) {
            this.Wed = Wed;
        }

        @JacksonXmlProperty(localName="Wed",isAttribute = true)
        public String getWed (){
            return this.Wed;
        }

        public void setSat(String Sat) {
            this.Sat = Sat;
        }

        @JacksonXmlProperty(localName="Sat",isAttribute = true)
        public String getSat (){
            return this.Sat;
        }

        public void setFri(String Fri) {
            this.Fri = Fri;
        }

        @JacksonXmlProperty(localName="Fri",isAttribute = true)
        public String getFri (){
            return this.Fri;
        }

        public void setMon(String Mon) {
            this.Mon = Mon;
        }

        @JacksonXmlProperty(localName="Mon",isAttribute = true)
        public String getMon (){
            return this.Mon;
        }

        public void setSun(String Sun) {
            this.Sun = Sun;
        }

        @JacksonXmlProperty(localName="Sun",isAttribute = true)
        public String getSun (){
            return this.Sun;
        }

    }

    @JacksonXmlRootElement(localName = "RateAmount")
    public static class RateAmount {
        /**
         * OnePerson
         */
        private String rateAmountType;

        /**
         * 1480
         */
        private String content;

        public void setRateAmountType(String rateAmountType) {
            this.rateAmountType = rateAmountType;
        }

        @JacksonXmlProperty(localName="rateAmountType",isAttribute = true)
        public String getRateAmountType (){
            return this.rateAmountType;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @JacksonXmlText
        public String getContent (){
            return this.content;
        }

    }

    @JacksonXmlRootElement(localName = "RateAmounts")
    public static class RateAmounts {
        /**
         * 
         */
        private List<RateAmount> RateAmount = new ArrayList<RateAmount>();

        public void setRateAmount(List<RateAmount> RateAmount) {
            this.RateAmount = RateAmount;
        }

        @JacksonXmlElementWrapper(useWrapping=false)
        @JacksonXmlProperty(localName="RateAmount")
        public List<RateAmount> getRateAmount (){
            return this.RateAmount;
        }

    }

    @JacksonXmlRootElement(localName = "RateUpdateSegment")
    public static class RateUpdateSegment {
        /**
         * 渠道
         */
        private String TargetGDS;

        /**
         * 
         */
        private TimeSpan TimeSpan = new TimeSpan();

        /**
         * 1KSTS
         */
        private String roomTypeCode;

        /**
         * true
         */
        private String rateChangeIndicator;

        /**
         * CNY
         */
        private String currencyCode;

        /**
         * 
         */
        private DOW DOW = new DOW();

        /**
         * 
         */
        private RateAmounts RateAmounts = new RateAmounts();

        /**
         * WHL01
         */
        private String rateCode;

        public void setTargetGDS(String TargetGDS) {
            this.TargetGDS = TargetGDS;
        }

        @JacksonXmlProperty(localName="TargetGDS")
        public String getTargetGDS (){
            return this.TargetGDS;
        }

        public void setTimeSpan(TimeSpan TimeSpan) {
            this.TimeSpan = TimeSpan;
        }

        @JacksonXmlProperty(localName="TimeSpan")
        public TimeSpan getTimeSpan (){
            return this.TimeSpan;
        }

        public void setRoomTypeCode(String roomTypeCode) {
            this.roomTypeCode = roomTypeCode;
        }

        @JacksonXmlProperty(localName="roomTypeCode")
        public String getRoomTypeCode (){
            return this.roomTypeCode;
        }

        public void setRateChangeIndicator(String rateChangeIndicator) {
            this.rateChangeIndicator = rateChangeIndicator;
        }

        @JacksonXmlProperty(localName="rateChangeIndicator")
        public String getRateChangeIndicator (){
            return this.rateChangeIndicator;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }

        @JacksonXmlProperty(localName="currencyCode")
        public String getCurrencyCode (){
            return this.currencyCode;
        }

        public void setDOW(DOW DOW) {
            this.DOW = DOW;
        }

        @JacksonXmlProperty(localName="DOW")
        public DOW getDOW (){
            return this.DOW;
        }

        public void setRateAmounts(RateAmounts RateAmounts) {
            this.RateAmounts = RateAmounts;
        }

        @JacksonXmlProperty(localName="RateAmounts")
        public RateAmounts getRateAmounts (){
            return this.RateAmounts;
        }

        public void setRateCode(String rateCode) {
            this.rateCode = rateCode;
        }

        @JacksonXmlProperty(localName="rateCode")
        public String getRateCode (){
            return this.rateCode;
        }

    }

    @JacksonXmlRootElement(localName = "RateUpdateSegments")
    public static class RateUpdateSegments {
        /**
         * 
         */
        private RateUpdateSegment RateUpdateSegment = new RateUpdateSegment();

        public void setRateUpdateSegment(RateUpdateSegment RateUpdateSegment) {
            this.RateUpdateSegment = RateUpdateSegment;
        }

        @JacksonXmlElementWrapper(useWrapping=false)
        @JacksonXmlProperty(localName="RateUpdateSegment")
        public RateUpdateSegment getRateUpdateSegment (){
            return this.RateUpdateSegment;
        }

    }

    @JacksonXmlRootElement(localName = "Property")
    public static class Property {
        /**
         * COL
         */
        private String chainCode;

        /**
         * FIDYY
         */
        private String hotelCode;

        /**
         * 
         */
        private RateUpdateSegments RateUpdateSegments = new RateUpdateSegments();

        public void setChainCode(String chainCode) {
            this.chainCode = chainCode;
        }

        @JacksonXmlProperty(localName="chainCode")
        public String getChainCode (){
            return this.chainCode;
        }

        public void setHotelCode(String hotelCode) {
            this.hotelCode = hotelCode;
        }

        @JacksonXmlProperty(localName="hotelCode")
        public String getHotelCode (){
            return this.hotelCode;
        }

        public void setRateUpdateSegments(RateUpdateSegments RateUpdateSegments) {
            this.RateUpdateSegments = RateUpdateSegments;
        }

        @JacksonXmlProperty(localName="RateUpdateSegments")
        public RateUpdateSegments getRateUpdateSegments (){
            return this.RateUpdateSegments;
        }

    }

    @JacksonXmlRootElement(localName = "Properties")
    public static class Properties {
        /**
         * 
         */
        private Property Property = new Property();

        public void setProperty(Property Property) {
            this.Property = Property;
        }

        @JacksonXmlProperty(localName="Property")
        public Property getProperty (){
            return this.Property;
        }

    }

    @JacksonXmlRootElement(localName = "FIDELIO_RateUpdateNotifRQ")
    public static class FIDELIO_RateUpdateNotifRQ {
        /**
         * 
         */
        private Properties Properties = new Properties();

        public void setProperties(Properties Properties) {
            this.Properties = Properties;
        }

        @JacksonXmlProperty(localName="Properties")
        public Properties getProperties (){
            return this.Properties;
        }

    }
}
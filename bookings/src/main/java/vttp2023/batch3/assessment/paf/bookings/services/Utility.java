package vttp2023.batch3.assessment.paf.bookings.services;

import org.bson.Document;

import vttp2023.batch3.assessment.paf.bookings.models.Address;
import vttp2023.batch3.assessment.paf.bookings.models.Listing;

public class Utility {

    public static Listing toListing(Document doc){
        Address address = new Address();
        Document add = (Document) doc.get("address");
        address.setStreet(add.getString("street"));
        address.setSuburb(add.getString("subrub"));
        address.setCountry(add.getString("country"));
        
        Listing listing = new Listing();
        listing.setId(doc.getString("_id"));
        listing.setDescription(doc.getString("description"));
        listing.setAddress(address);
        Document pic = (Document) doc.get("images");
        listing.setImage(pic.getString("picture_url"));
        listing.setPrice(doc.getDouble("price"));
        listing.setAmenities(doc.getList("amenities", String.class));

        return listing;
    }
    
}

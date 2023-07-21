package vttp2023.batch3.assessment.paf.bookings.services;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.ObjectError;

import vttp2023.batch3.assessment.paf.bookings.models.Booking;
import vttp2023.batch3.assessment.paf.bookings.models.Listing;
import vttp2023.batch3.assessment.paf.bookings.repositories.ListingsRepository;

@Service
public class ListingsService {

	@Autowired
	private ListingsRepository listingsRepository;
	
	//TODO: Task 2
	public List<String> getAllCountries(){
		return listingsRepository.getAllCountries();
	}
	
	//TODO: Task 3
		public List<Listing> searchResults(String country, int person, int min, int max){
		
		List<Document> docs = listingsRepository.searchResults(country, person, min, max);

		List<Listing> results = docs.stream()
			.map(Utility :: toListing)
			.toList();

		return results;
	}

	//TODO: Task 4
	public Optional<Listing> getListingById(String id){
		Optional<Document> doc = listingsRepository.getListingById(id);

		if (doc.isEmpty()){
			return Optional.empty();
		}
		
		return Optional.of(Utility.toListing(doc.get()));
	}
	

	//TODO: Task 5
	@Transactional
	public List<ObjectError> bookListing(Booking booking){

		List<ObjectError> errors = new LinkedList<>();

		String roomId = booking.getAccId();
		int bookDays = booking.getDuration();
		int availablity = listingsRepository.checkVacancy(roomId);

		if (bookDays > availablity){
			ObjectError error = new ObjectError("err", "Accommodation is not available!");
			errors.add(error);
			return errors;
		}

		String bookingId = UUID.randomUUID().toString().substring(0, 8);
		booking.setResv_id(bookingId);
		Boolean booked = listingsRepository.updateVacancy(booking);

		if(!booked){
			ObjectError err = new ObjectError("err", "Something went wrong!");
			errors.add(err);
		}

		Boolean updated = listingsRepository.updateVacancy(booking);

		if(!updated){
			ObjectError err = new ObjectError("err", "Something went wrong!");
			errors.add(err);
		}
		
		return errors;

	}


}

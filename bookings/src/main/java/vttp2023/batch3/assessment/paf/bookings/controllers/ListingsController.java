package vttp2023.batch3.assessment.paf.bookings.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import vttp2023.batch3.assessment.paf.bookings.models.Booking;
import vttp2023.batch3.assessment.paf.bookings.models.Listing;
import vttp2023.batch3.assessment.paf.bookings.services.ListingsService;

@Controller
@RequestMapping("/")
public class ListingsController {

	@Autowired
	private ListingsService listingsService;

	//TODO: Task 2
	@GetMapping
	public String getWelcomePage(Model model){
		List<String> countries = listingsService.getAllCountries();
		model.addAttribute("countries", countries);
		return "welcome";
	}

	//TODO: Task 3
	@GetMapping ("/search")
	public String searchAccomodations(@RequestParam String country, @RequestParam int person,
	@RequestParam int min, @RequestParam int max, Model model){
		model.addAttribute("country", country);
		List<Listing> listings = listingsService.searchResults(country, person, min, max);
		model.addAttribute("listings", listings);
		return "results";
	}
	
	//TODO: Task 4
	@GetMapping("/{id}")
	public String getListingDetails(@PathVariable String id, Model model, HttpServletRequest request){
		Optional<Listing> result = listingsService.getListingById(id);

		if (result.isEmpty()){
			return "not-found";
		}

		Listing listing = result.get();
		model.addAttribute("listing", listing);
		Booking booking = new Booking();
		model.addAttribute("booking", booking);
		String referer = request.getHeader("referer");
		model.addAttribute("referer", referer);
		return "details";
	}
	

	//TODO: Task 5
	@PostMapping("/book")
	public String bookListing(@Valid Booking booking, Listing listing, BindingResult result, Model model, HttpServletRequest request){

		List <ObjectError> errors = listingsService.bookListing(booking);
		if (!errors.isEmpty()){
			for (ObjectError e : errors){
				result.addError(e);
			}

			String referer = request.getHeader("referer");
			return referer;
		}
		String bookingId = booking.getResv_id();
		model.addAttribute("id", bookingId);
		return "booked";
		
	}


}

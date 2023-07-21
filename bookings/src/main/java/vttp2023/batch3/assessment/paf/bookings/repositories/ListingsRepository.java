package vttp2023.batch3.assessment.paf.bookings.repositories;

import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import vttp2023.batch3.assessment.paf.bookings.models.Booking;

@Repository
public class ListingsRepository {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private final String CHECK_VACANCY = "select vacancy from acc_occupancy where acc_id = ?";
	public final String SAVE_BOOKING = "insert into reservations(resv_id, name, email, acc_id, arrival_date, duration) values (?, ?, ?, ?, ?, ?)";
	public final String UPDATE_VACANCY = "update acc_occupancy set vacancy = vacancy - ? where acc_id = ?";

	//TODO: Task 2

	/*
	 db.listings.distinct('address.country')
	 */
	public List<String> getAllCountries(){
		return mongoTemplate.findDistinct(new Query(), "address.country", "listings", String.class);
	}

	//TODO: Task 3

	/*
	db.listings.find({
		$and: [
			{'address.country': {
				$regex: "country",
				$options: "i"
			}},
			{accommodates: 2},
			{price: { $gte : 1 } },
			{price: { $lte : 10000} }
		]
    }).sort({ price: -1 })
	 */
	public List<Document> searchResults(String country, int person, int min, int max){

		Criteria criteria = new Criteria().andOperator(
			Criteria.where("address.country").regex(country, "i"),
			Criteria.where("accommodates").is(person),
			Criteria.where("price").gte(min),
			Criteria.where("price").lte(max)
		);


		Query query = Query.query(criteria)
		.with(Sort.by(Sort.Direction.DESC, "price"));

		return mongoTemplate.find(query, Document.class, "listings");
	}

	//TODO: Task 4

	/*
	 db.listings.find({
    	_id: '16134812'
	})
	 */
	public Optional<Document> getListingById(String id){
		Criteria criteria = Criteria.where("_id")
			.is(id);

		Query query = Query.query(criteria);

		List<Document> result = mongoTemplate.find(query, Document.class, "listings");

		if (result.isEmpty()){
			return Optional.empty();
		}

		return Optional.of(result.get(0));
	}

	//TODO: Task 5
	public Integer checkVacancy(String id){
		return jdbcTemplate.queryForObject(CHECK_VACANCY, 
            BeanPropertyRowMapper.newInstance(Integer.class), id);
	}

	public Boolean saveBooking(Booking booking){
		int result = 0;
		result = jdbcTemplate.update(CHECK_VACANCY, booking.getResv_id(), booking.getName(),
			booking.getEmail(), booking.getAcc_id(), booking.getArrival_date(), booking.getDuration());

		return result > 0 ? true : false;

	}

	public Boolean updateVacancy(Booking booking){
		int result = 0;
		result = jdbcTemplate.update(UPDATE_VACANCY, booking.getDuration(), booking.getAcc_id());

		return result > 0 ? true : false;
	}


}

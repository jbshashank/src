package com.ayushya.spring.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayushya.spring.bean.Technician;
import com.ayushya.spring.bean.Tickets;
import com.ayushya.spring.repository.TechnicianRepository;
import com.ayushya.spring.repository.TicketsRepository;
import com.ayushya.spring.service.AssignService;
import com.ayushya.spring.service.NextSequenceService;
import com.ayushya.spring.service.TicketService;
import com.ayushya.spring.service.ClosedTicketService;

@RestController
@RequestMapping("/tickets")
public class TicketsController
{

	private static List<Technician> SE = new ArrayList<Technician>();
	@Autowired
	private TicketsRepository repository;

	@Autowired
	private TechnicianRepository technicianRepository;

	@Autowired
	private NextSequenceService nextSequenceService;

	@Autowired
	private TicketService ticketService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public Iterable<Tickets> getAllTickets(Pageable pageable)
	{
		return repository.findAll(pageable);
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public List<Tickets> getAllTickets1(@RequestParam("status") String status,
			Pageable pageable)
	{

		return filterTickets(status, repository.findAll(pageable));

	}

	/**
	 * filter ticket
	 * 
	 * @param _id
	 * @return
	 */
	private List<Tickets> filterTickets(String status,
			Iterable<Tickets> iterTickets)
	{

		Iterator ticketsIter = iterTickets.iterator();
		Tickets tkt = null;
		List<Tickets> lstTickets = new ArrayList<Tickets>();
		while (ticketsIter.hasNext())
		{
			tkt = (Tickets) ticketsIter.next();
			if (tkt.getTicket_status() != null
					&& tkt.getTicket_status()
					.equalsIgnoreCase(status))
			{
				lstTickets.add(tkt);
			}
		}

		return lstTickets;

	}

	@RequestMapping(value = "/{_id}", method = RequestMethod.GET)
	public Tickets getOneTicket(@PathVariable String _id)
	{
		return repository.findOne(_id);
	}

	@RequestMapping(value = "/closed", method = RequestMethod.GET)
	public List<Tickets> getClosedTickets()
	{
		return new ClosedTicketService().getClosedTickets(repository.findAll());
	}

	@RequestMapping(value = "/city/{city_filter}", method = RequestMethod.GET)
	public Iterable<Tickets> getTicketsInCity(@PathVariable String city_filter,
			Pageable pageable)
	{
		return (Iterable<Tickets>) repository.findByCity(city_filter, pageable);
	}

	@RequestMapping(value = "/visit_date/{visit_date:.+}", method = RequestMethod.GET)
	public Iterable<Tickets> getTicketsBasedOnVisitDate(@PathVariable("visit_date") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date visit_date,
			Pageable pageable)
	{
		return (Iterable<Tickets>) repository.findTicketsByVisit_date(visit_date, pageable);
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public Tickets createTicket(@Valid @RequestBody Tickets tick) throws ParseException
	{
		tick.set_id(nextSequenceService.getNextSequence("customSequences",
				new SimpleDateFormat("ddMMyy").format(new Date())));
		tick.setTicket_status("Open");
		tick.setDate_of_post(new SimpleDateFormat("ddMMyyhhmmss").format(new Date()));
//		List<technician> data = ticketService.getEmployeeData(SE);
//		String id = new AssignService().assignTicket(data, tick.getCity());
//
//		if (id != null)
//		{
//			tick.setTech_name(id);
//			technician tech = new technician();
//			tech = technicianRepository.findOne(id);
//			tech.setNo_assigned(technicianRepository.findOne(id)
//					.getNo_assigned()
//					+ 1);
//			technicianRepository.save(tech);
//		}
//		String visitDate = "12-12-2019";
//		String visitTime = "10:05 AM";
//		// Conversion
//		Date sourceDate = new SimpleDateFormat("mm-dd-yyyy:hh:mm a").parse(visitDate+":"+visitTime);
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//		sdf.setTimeZone(TimeZone.getTimeZone("IST"));
//		String text = sdf.format(sourceDate);
		repository.save(tick);
		return tick;
	}

	@RequestMapping(value = "/ticket/{ticket_id}/{status}", method = RequestMethod.PUT)
	public Tickets updateTicketStatus(@PathVariable String ticket_id,@PathVariable String status)
	{
		return ticketService.updateTicketStatus(ticket_id,status);
	}

	@RequestMapping(value = "/{_id}", method = RequestMethod.PUT)
	public Tickets updateTicket(@PathVariable String _id, @Valid @RequestBody Tickets ticket) {
		return ticketService.updateTicket(_id,ticket);
	}
}

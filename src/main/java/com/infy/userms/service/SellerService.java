package com.infy.userms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.infy.userms.dto.BuyerDTO;
import com.infy.userms.dto.LoginDTO;
import com.infy.userms.dto.OrderDetailsDTO;
import com.infy.userms.dto.ProductsOrderedDTO;
import com.infy.userms.dto.SellerDTO;
import com.infy.userms.entity.Buyer;
import com.infy.userms.entity.Seller;
import com.infy.userms.repository.BuyerRepository;
import com.infy.userms.repository.SellerRepository;
import com.infy.userms.validator.Validator;
@Service
public class SellerService {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	SellerRepository sellerRepo;
	@Autowired
	BuyerRepository buyerRepo;
	//Register Seller
		public void createSeller(SellerDTO sellerDTO) throws Exception
		{
			logger.info("Creation request for seller {}", sellerDTO);
			Seller seller = sellerDTO.createEntity();
			Validator.validateSeller(seller);
			Optional<Seller> phone = sellerRepo.findByPhonenumber(seller.getPhonenumber());
			Optional<Seller> email = sellerRepo.findByEmail(seller.getEmail());
			
			if(email.isPresent()) {
				throw new Exception("EMAIL_ALREADY_EXISTS");
			}	
			if(phone.isPresent())
			{
				if(seller.getIsActive() == 0) {
					throw new Exception("ACCOUNT_ALREADY_REGISTERED");
				}
				throw new Exception("PHONE_NUMBER_EXISTS");
			}
			seller.setIsActive(1);
	
			sellerRepo.save(seller);

		}
		//get all sellers
		public List<SellerDTO> getAllSellers()
		{
			logger.info("All seller details");

			List<Seller> seller = sellerRepo.findAll();
			List<SellerDTO> sellerDTO = new ArrayList<>();
			for(Seller s : seller)
			{
				SellerDTO sellersDTO = SellerDTO.valueOf(s);
				sellerDTO.add(sellersDTO);
			}
			return sellerDTO;

		}
		
		//seller login
		public boolean login(LoginDTO loginDTO)throws Exception {
			logger.info("Login request for seller ", loginDTO.getEmail(),loginDTO.getPassword());
			List<Seller> seller = sellerRepo.findAll();
			int flag=0;
			for(Seller s : seller)
			{
			
				if(s.getEmail().equals(loginDTO.getEmail()) && s.getPassword().equals(loginDTO.getPassword()))
				{
					 flag=1;
					return true;
				}
				else
					flag = 0;
			}
			if(flag==0)
			{
				throw new Exception("INVALID_CREDENTIALS");
			}
			return false;
		}
		//inactive buyer 
				public Boolean InactiveSeller(SellerDTO sellerDTO)throws Exception
				{
					logger.info("inactive seller acc");
					Optional<Seller> b  = sellerRepo.findById(sellerDTO.getSellerId());
					if(b.isPresent())
					{
						Seller seller = b.get();
						seller.setIsActive(sellerDTO.getSellerId());
						sellerRepo.save(seller);
						return true;
					}
					else
					{
						throw new Exception("Seller not found");
					}
				}		
		//delete specific seller
		public Boolean deleteSpecificSeller(Integer sellerid)
		{
			Boolean b = null;
			Optional<Seller> seller = sellerRepo.findById(sellerid);
			if(seller.isPresent())
			{
				Seller s = seller.get();
				if(s.getIsActive()==1)
				{
					sellerRepo.deleteById(sellerid);
					b=true;
				}
				else
				{
					b = false;
				}
			}
			else
			{
				b=false;
			}
			return b;
		}
		//get specific seller
		public SellerDTO getSpecificSeller(Integer sellerid)
		{
			logger.info("Specific seller details");
			SellerDTO seller = null; 
			Optional<Seller> s = sellerRepo.findById(sellerid);
			if(s.isPresent())
			{
				Seller sellers = s.get();
				seller = SellerDTO.valueOf(sellers); 
			}
		    return seller; 	
		}

		//view orders placed on their product
		public List<OrderDetailsDTO> viewPlacedOrdersOnProduct(OrderDetailsDTO[] orderDetails, Integer prodid)
		{
			List<OrderDetailsDTO> orders = new ArrayList<OrderDetailsDTO>();
			for(OrderDetailsDTO o:orderDetails)
			{
				List<ProductsOrderedDTO> products = o.getProductsOrdered();
				for(ProductsOrderedDTO p:products)
				{
					if(p.getProdId().equals(prodid))
					{
						orders.add(o);
					}
				}
				
			}
			return orders;
			
		}
	
	//set reward points and priviledged
		public void setReward(OrderDetailsDTO[] orders, BuyerDTO[] buyers)
		{
			for(BuyerDTO b : buyers)
			{
				for(OrderDetailsDTO o : orders)
				{
					if(b.getBuyerId().equals(o.getBuyerid()))
					{
						Integer pri=0;
						Integer reward = 0;
						reward = (int)o.getAmount()/100;
						if(reward>=10000)
						{
							pri = 1;
						}
						Optional<Buyer> buyer = buyerRepo.findById(o.getBuyerid());
						Buyer m = buyer.get();
						m.setRewardPoints(reward+m.getRewardPoints());
						m.setIsPrivileged(pri);
						buyerRepo.save(m);
					}
				}
			}
		}
		
}


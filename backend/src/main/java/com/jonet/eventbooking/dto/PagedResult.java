package com.jonet.eventbooking.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PagedResult {
	 private List<?> list;
	 private int totalPage;
	 
	 public static PagedResult of(List<?> list, int totalPage) {
		 return new PagedResult(list, totalPage);
	 }
}

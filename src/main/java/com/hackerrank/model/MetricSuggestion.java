package com.hackerrank.model;

public class MetricSuggestion {
	    private String metric;
	    private String status;
	    private String suggestion;
	    
		public MetricSuggestion(String metric, String status, String suggestion) {
			super();
			this.metric = metric;
			this.status = status;
			this.suggestion = suggestion;
		}

		public String getMetric() {
			return metric;
		}

		public void setMetric(String metric) {
			this.metric = metric;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getSuggestion() {
			return suggestion;
		}

		public void setSuggestion(String suggestion) {
			this.suggestion = suggestion;
		}
	    

}

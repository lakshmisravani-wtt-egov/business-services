package com.tarento.analytics.handler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.tarento.analytics.dto.AggregateDto;
import com.tarento.analytics.dto.Data;
import com.tarento.analytics.dto.Plot;
import com.tarento.analytics.enums.ChartType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles Elastic search consolidate responses
 */
public interface IResponseHandler {

	public static final Logger logger = LoggerFactory.getLogger(IResponseHandler.class);

	public static final String API_CONFIG_JSON = "ChartApiConfig.json";
	public static final String AGGS_PATH = "aggregationPaths";

	public static final String CHART_NAME = "chartName";
	public static final String CHART_TYPE = "chartType";
	public static final String DRILL_CHART = "drillChart";
	public static final String VALUE_TYPE = "valueType";
	public static final String FILTER_KEYS = "filterKeys";

	public final String ASC = "asc";
	public final String DESC = "desc";
	public final String RANK = "Rank";
	public final String AGGREGATIONS = "aggregations";
	public final String PLOT_LABEL = "plotLabel";
	public final String LIMIT = "limit";
	public final String ORDER = "order";
	public final String ACTION = "action";
	public final String TYPE_MAPPING = "pathDataTypeMapping";

	public static String BUCKETS = "buckets";
	public static String KEY = "key";
	public static String VALUE = "value";

	public static Double BOUNDARY_VALUE = 50.0;

	/**
	 * Translate the consolidated/aggregated response
	 *
	 * @param chartId
	 * @param aggregations
	 * @return
	 * @throws IOException
	 */
	public AggregateDto translate(String chartId, ObjectNode aggregations) throws IOException;

	/**
	 * Prepare aggregated dato for a chart node
	 *
	 * @param chartNode
	 * @param dataList - data plots object
	 * @return
	 */
	default AggregateDto getAggregatedDto(JsonNode chartNode, List<Data> dataList) {
		AggregateDto aggregateDto = new AggregateDto();
		aggregateDto.setDrillDownChartId(chartNode.get(DRILL_CHART).asText());
		ChartType chartType = ChartType.fromValue(chartNode.get(CHART_TYPE).asText());
		aggregateDto.setChartType(chartType);
		aggregateDto.setData(dataList);
		if(null!=chartNode.get(FILTER_KEYS))
			aggregateDto.setFilter((ArrayNode) chartNode.get(FILTER_KEYS));
		return aggregateDto;
	}

	/**
	 * Append computed field for a given Data, for its existing fields
	 * computes as partfield/wholefield * 100
	 *
	 * @param data
	 * @param newfield
	 * @param partField
	 * @param wholeField
	 */
	default void addComputedField(Data data, String newfield, String partField, String wholeField) {
		try {
			Map<String, Plot> plotMap = data.getPlots().stream().parallel().collect(Collectors.toMap(Plot::getName, Function.identity()));

			if (plotMap.get(partField).getValue() == 0.0 || plotMap.get(wholeField).getValue() == 0.0) {
				data.getPlots().add(new Plot(newfield, 0.0, "percentage"));
			} else {
				double fieldValue = plotMap.get(partField).getValue() / plotMap.get(wholeField).getValue() * 100;
				data.getPlots().add(new Plot(newfield, fieldValue, "percentage"));

			}
		} catch (Exception e) {
			e.printStackTrace();
			data.getPlots().add(new Plot(newfield, 0.0, "percentage"));
		}

	}

	/**
	 * Computes the percentage from 0th and 1st index of list
	 * Ex: 0th element/1st element * 100
	 * @param values
	 * @return
	 */
	default Double percentageValue(List<Double> values) {
		double val = (values.get(0)/values.get(1) * 100);
		return (values.size() > 1 && values.get(0) != 0.0 && values.get(1) != 0.0) ? val : 0.0;
	}


	/**
	 * Computes the percentage from 1st & 2nd element of collection
	 * Ex: first element/second element * 100
	 * @param values
	 * @return
	 */
	default Double getPercentage(Map<String, Double> values, String partField, String wholeField) {

		double val = (values.get(partField)/ values.get(wholeField) * 100);
		return (values.size() > 1 && values.get(partField) != 0.0 && values.get(wholeField) != 0.0)  ? val: 0.0;
	}

	/**
	 * Adding missing plot elements to Data
	 * @param plotKeys - all required plot key
	 * @param data
	 * @param symbol
	 */
	default void appendMissingPlot(Set<String> plotKeys, Data data, String symbol) {

		Map<String, Plot> map = data.getPlots().stream().collect(Collectors.toMap(Plot::getName, Function.identity()));
		plotKeys.removeAll(map.keySet());
		plotKeys.forEach(plKey -> {
			map.put(plKey, new Plot(plKey, new Double("0"), symbol));
		});

	}

}
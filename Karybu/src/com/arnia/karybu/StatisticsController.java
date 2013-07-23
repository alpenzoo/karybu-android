package com.arnia.karybu;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine.Type;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arnia.karybu.classes.KarybuArrayList;
import com.arnia.karybu.classes.KarybuDayStats;
import com.arnia.karybu.classes.KarybuHost;

public class StatisticsController extends KarybuFragment implements
		OnClickListener {

	private View view;
	private KarybuArrayList array;
	private GraphicalView graphicalView;
	private TextView txtGraphDate;
	private TextView txtLastWeekCount;
	private XYMultipleSeriesRenderer renderer;
	private Date startDate;
	private Date endDate;
	private int maxVisitor;
	private final double ONE_DAY = 24 * 60 * 60 * 1000;
	private int currentRange; // Current visible range of the graph
	private int rangeCount;
	private final int DAYS_PER_RANGE = 7;
	private ImageButton btnArrowLeft;
	private ImageButton btnArrowRight;
	private boolean isRefreshing;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.layout_statistics, container, false);
		txtGraphDate = (TextView) view.findViewById(R.id.txt_graph_date);
		txtGraphDate.setText("");
		txtLastWeekCount = (TextView) view
				.findViewById(R.id.txt_last_week_count);
		txtLastWeekCount.setText("");

		btnArrowLeft = (ImageButton) view
				.findViewById(R.id.btn_statistic_arrow_left);
		btnArrowLeft.setOnClickListener(this);
		btnArrowLeft.setEnabled(false);
		btnArrowRight = (ImageButton) view
				.findViewById(R.id.btn_statistic_arrow_right);
		btnArrowRight.setOnClickListener(this);
		btnArrowRight.setEnabled(false);

		return view;

	}

	@Override
	public void onStart() {
		super.onStart();
		refreshStatistic();
	}

	public void refreshStatistic() {
		if (isRefreshing)
			return;
		GetStatisticsAsyncTask task = new GetStatisticsAsyncTask();
		task.execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_statistic_arrow_left:
			moveGraphToLeft();
			break;
		case R.id.btn_statistic_arrow_right:
			moveGraphToRight();
			break;
		}
	}

	// Async task for loading the statistics
	private class GetStatisticsAsyncTask extends AsyncTask<Void, Void, Void> {
		String response;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			isRefreshing = true;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// making the request
			response = KarybuHost
					.getINSTANCE()
					.postRequest(
							"/index.php?module=mobile_communication&act=procmobile_communicationViewerData");

			// parsing the response
			Serializer serializer = new Persister();

			try {
				array = serializer.read(KarybuArrayList.class, response, false);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void context) {
			// check if the user is still logged in

			isRefreshing = false;

			if (array != null && array.stats != null) {
				buildGraph(array.stats);
			}

			super.onPostExecute(context);
		}

		@SuppressLint("SimpleDateFormat")
		private void buildGraph(ArrayList<KarybuDayStats> stats) {
			if (stats.size() == 0)
				return;

			rangeCount = (int) Math.ceil(1.0 * stats.size() / DAYS_PER_RANGE);

			// Time Chart
			String startDateStr = stats.get(0).date;
			String endDateStr = stats.get(stats.size() - 1).date;
			SimpleDateFormat spd = new SimpleDateFormat("yyyyMMdd");
			String graphDateStr = "";
			startDate = new Date();
			endDate = new Date();
			try {
				startDate = spd.parse(startDateStr);
				endDate = spd.parse(endDateStr);
				spd = new SimpleDateFormat("MMMdd");
				graphDateStr = String.format("%s-%s", spd.format(startDate),
						spd.format(endDate));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			txtGraphDate.setText(graphDateStr);

			TimeSeries series = new TimeSeries("Visit Date");

			spd = new SimpleDateFormat("yyyyMMdd");
			maxVisitor = 0;
			int lastWeekCount = 0;
			for (int i = 0; i < stats.size(); i++) {
				KarybuDayStats stat = stats.get(i);
				int visitorCount = Integer.parseInt(stat.unique_visitor);
				Date date = new Date();
				try {
					date = spd.parse(stat.date);
				} catch (ParseException e) {
					e.printStackTrace();
				}

				if (visitorCount > maxVisitor)
					maxVisitor = visitorCount;
				if (i > (stats.size() - 7))
					lastWeekCount = lastWeekCount + visitorCount;

				series.add(date, visitorCount);
			}
			NumberFormat numberFormat = NumberFormat.getNumberInstance();
			txtLastWeekCount.setText(numberFormat.format(lastWeekCount));

			XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
			dataset.addSeries(series);

			renderer = new XYMultipleSeriesRenderer();

			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(getResources().getColor(R.color.visitor_graph));
			r.setLineWidth(2);
			FillOutsideLine fill = new FillOutsideLine(Type.BELOW);
			fill.setColor(getResources().getColor(R.color.bg_visitor_graph));
			r.addFillOutsideLine(fill);
			renderer.addSeriesRenderer(r);

			renderer.setMargins(new int[] { 20, 30, 5, 20 }); // Top, left,
																// bottom, right
			renderer.setLabelsTextSize(17);
			renderer.setYAxisMin(0);
			renderer.setYAxisMax(maxVisitor * 1.2);
			renderer.setYLabels(5);
			currentRange = rangeCount;
			if (stats.size() > DAYS_PER_RANGE) {
				renderer.setXLabels(stats.size() / 2);
				renderer.setRange(new double[] {
						endDate.getTime() - (ONE_DAY * DAYS_PER_RANGE),
						endDate.getTime(), 0, maxVisitor * 1.2 });
				btnArrowLeft.setEnabled(true);
			} else {
				renderer.setXLabels(stats.size());
				renderer.setRange(new double[] { startDate.getTime(),
						endDate.getTime() + ONE_DAY, 0, maxVisitor * 1.2 });
			}
			renderer.setAxesColor(Color.LTGRAY);
			renderer.setLabelsColor(Color.LTGRAY);

			renderer.setShowGrid(true);
			renderer.setBackgroundColor(Color.TRANSPARENT);
			renderer.setXLabelsAlign(Align.CENTER);
			renderer.setYLabelsAlign(Align.LEFT);
			renderer.setZoomButtonsVisible(false);
			renderer.setShowLegend(false);
			renderer.setPanEnabled(false);
			renderer.setZoomEnabled(false);

			numberFormat = new NumberFormat() {

				private static final long serialVersionUID = 1L;

				@Override
				public Number parse(String string, ParsePosition position) {
					throw new UnsupportedOperationException();
				}

				@Override
				public StringBuffer format(long value, StringBuffer buffer,
						FieldPosition field) {
					if (value == 0)
						return buffer.append("");
					else if (value < 1000)
						return buffer.append(value);
					else {
						double newValue = value / 1000;
						DecimalFormat decimalFormat = new DecimalFormat("#.#");
						return buffer.append(decimalFormat.format(newValue)
								+ "k");
					}
				}

				@Override
				public StringBuffer format(double value, StringBuffer buffer,
						FieldPosition field) {
					if (value == 0)
						return buffer.append("");
					else if (value < 1000) {
						return buffer.append((long) value);
					} else {
						value = value / 1000;
						DecimalFormat decimalFormat = new DecimalFormat("#.#");
						return buffer.append(decimalFormat.format(value) + "k");
					}

				}
			};

			renderer.setLabelFormat(numberFormat);

			graphicalView = ChartFactory.getTimeChartView(getActivity(),
					dataset, renderer, "E");

			LinearLayout lytStatistic = (LinearLayout) view
					.findViewById(R.id.lyt_visitor_statistic);
			lytStatistic.removeAllViews();
			lytStatistic.addView(graphicalView);
		}

	}

	private void moveGraphToLeft() {
		if (currentRange == 0)
			return;

		currentRange--;

		double xStart, xEnd;

		if (currentRange == 0) {
			btnArrowLeft.setEnabled(false);
			xStart = startDate.getTime();
			xEnd = xStart + (DAYS_PER_RANGE * ONE_DAY);
		} else {
			xStart = startDate.getTime()
					+ (currentRange * DAYS_PER_RANGE * ONE_DAY);
			xEnd = xStart + (DAYS_PER_RANGE * ONE_DAY);
		}

		renderer.setRange(new double[] { xStart, xEnd, 0, maxVisitor * 1.2 });
		graphicalView.repaint();

		btnArrowRight.setEnabled(true);
	}

	private void moveGraphToRight() {
		if (currentRange == rangeCount)
			return;

		currentRange++;

		double xStart, xEnd;

		if (currentRange == rangeCount) {
			btnArrowRight.setEnabled(false);
			xEnd = endDate.getTime();
			xStart = xEnd - (DAYS_PER_RANGE * ONE_DAY);
		} else {
			xStart = startDate.getTime()
					+ (currentRange * DAYS_PER_RANGE * ONE_DAY);
			xEnd = xStart + (DAYS_PER_RANGE * ONE_DAY);
		}

		renderer.setRange(new double[] { xStart, xEnd, 0, maxVisitor * 1.2 });
		graphicalView.repaint();

		btnArrowLeft.setEnabled(true);
	}
}

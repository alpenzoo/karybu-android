package com.arnia.karybu;

import java.text.ParseException;
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.layout_statistics, container, false);

		ImageButton btnArrowLeft = (ImageButton) view
				.findViewById(R.id.btn_statistic_arrow_left);
		btnArrowLeft.setOnClickListener(this);
		ImageButton btnArrowRight = (ImageButton) view
				.findViewById(R.id.btn_statistic_arrow_right);
		btnArrowRight.setOnClickListener(this);

		GetStatisticsAsyncTask task = new GetStatisticsAsyncTask();
		task.execute();

		return view;

	}

	// Async task for loading the statistics
	private class GetStatisticsAsyncTask extends AsyncTask<Void, Void, Void> {
		String response;

		@Override
		protected Void doInBackground(Void... params) {
			// making the request
			response = KarybuHost
					.getINSTANCE()
					.getRequest(
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

			if (array != null && array.stats != null) {
				buildGraph(array.stats);
			}

			super.onPostExecute(context);
		}

		@SuppressLint("SimpleDateFormat")
		private void buildGraph(ArrayList<KarybuDayStats> stats) {
			if (stats.size() == 0)
				return;

			String startDateStr = stats.get(0).date;
			String endDateStr = stats.get(stats.size() - 1).date;
			SimpleDateFormat spd = new SimpleDateFormat("yyyyMMdd");
			String graphDateStr = "";
			Date startDate = new Date();
			Date endDate = new Date();
			try {
				startDate = spd.parse(startDateStr);
				endDate = spd.parse(endDateStr);
				spd = new SimpleDateFormat("MMMdd");
				graphDateStr = String.format("%s-%s", spd.format(startDate),
						spd.format(endDate));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			TextView txtGraphDate = (TextView) view
					.findViewById(R.id.txt_graph_date);
			txtGraphDate.setText(graphDateStr);

			TimeSeries series = new TimeSeries("Visit Date");

			spd = new SimpleDateFormat("yyyyMMdd");
			int maxVisitor = 0;
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
			TextView txtLastWeekCount = (TextView) view
					.findViewById(R.id.txt_last_week_count);
			txtLastWeekCount.setText(lastWeekCount + "");

			XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
			dataset.addSeries(series);

			XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

			renderer.setAxisTitleTextSize(16);
			renderer.setChartTitleTextSize(20);
			renderer.setLabelsTextSize(15);
			renderer.setLegendTextSize(15);
			renderer.setPointSize(5f);
			renderer.setMargins(new int[] { 20, 30, 15, 20 });

			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(getResources().getColor(R.color.visitor_graph));
			r.setFillPoints(true);
			r.setLineWidth(2);
			FillOutsideLine fill = new FillOutsideLine(Type.BELOW);
			fill.setColor(getResources().getColor(R.color.bg_visitor_graph));
			r.addFillOutsideLine(fill);
			renderer.addSeriesRenderer(r);

			renderer.setXAxisMin(startDate.getTime());
			renderer.setXAxisMax(endDate.getTime());
			renderer.setYAxisMin(0);
			renderer.setYAxisMax(maxVisitor * 1.2);
			renderer.setXLabels(8);
			renderer.setYLabels(5);
			renderer.setPanLimits(new double[] { 1, 8, 0, maxVisitor * 1.2 });
			renderer.setAxesColor(Color.LTGRAY);
			renderer.setLabelsColor(Color.LTGRAY);

			renderer.setShowGrid(true);
			renderer.setApplyBackgroundColor(true);
			renderer.setBackgroundColor(Color.TRANSPARENT);
			renderer.setXLabelsAlign(Align.CENTER);
			renderer.setYLabelsAlign(Align.RIGHT);
			renderer.setZoomButtonsVisible(false);
			renderer.setShowLegend(false);

			graphicalView = ChartFactory.getTimeChartView(getActivity(),
					dataset, renderer, "E");

			LinearLayout lytStatistic = (LinearLayout) view
					.findViewById(R.id.lyt_visitor_statistic);
			lytStatistic.addView(graphicalView);

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_statistic_arrow_left:
			graphicalView.setScrollX(11);
			break;
		case R.id.btn_statistic_arrow_right:
			graphicalView.setScrollX(11);
			break;
		}
	}

}

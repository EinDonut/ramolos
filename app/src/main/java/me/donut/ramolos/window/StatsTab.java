package me.donut.ramolos.window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import me.donut.ramolos.Ramolos;
import me.donut.ramolos.Utils;
import me.donut.ramolos.stats.Statistic;
import me.donut.ramolos.stats.StatisticManager;

public class StatsTab extends JPanel {

	public JLabel footerLableDate;
	private JTable infoTable;
	private JTextField headerSearch;
	private int selectedInterval = Statistic.INTERVAL_ALLTIME;
	private DefaultTableModel model;
	private TableRowSorter<DefaultTableModel> sorter;
	private final ImageIcon iconHide = Utils.getIcon("/images/stats/hide.png");
	private final ImageIcon iconShow = Utils.getIcon("/images/stats/show.png");
	private final ImageIcon iconWarning = Utils.getIcon("/images/stats/warning.png");
	private final ImageIcon iconHighlight = Utils.getIcon("/images/stats/highlight.png");
	private final ImageIcon iconEmpty = new ImageIcon();
	private String filterText = "";
	private PlayerStatsWindow playerStatsWindow;

	private MouseMotionListener tableHoverListener;
	private MouseListener tableClickListener;
	private FocusListener headerFocusListener;
	private DocumentListener searchChangeListener;

	public StatsTab() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(416, 700));
		initListener();

		// HEADER //

		JPanel header = new JPanel();
		header.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		header.setLayout(new GridLayout(1, 2));

		headerSearch = new JTextField("Filter");
		headerSearch.setForeground(Color.GRAY);
		headerSearch.setFont(Window.getCustomFont());
		headerSearch.addFocusListener(headerFocusListener);
		headerSearch.getDocument().addDocumentListener(searchChangeListener);

		JComboBox<String> headerInterval = new JComboBox<>(new String[] {"Insgesamt", "30 Tage", "Heute", "Versteckt"});
		headerInterval.setPreferredSize(new Dimension(150, 40));
		headerInterval.setFont(Window.getCustomFont());
		headerInterval.setSelectedIndex(selectedInterval);
		headerInterval.addActionListener(e -> {
			selectedInterval = headerInterval.getSelectedIndex();
			for (Statistic stat : Ramolos.getInstance().getStatisticManager().getStatistics().values()) {
				stat.update(true, true);
			}
			updateFilter();
		});

		header.add(headerSearch);
		header.add(headerInterval);

		model = new DefaultTableModel() {
            public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        };

		infoTable = new JTable(model);
		infoTable.setFont(Window.getCustomFont());
		infoTable.setTableHeader(null);
		infoTable.setFocusable(false);
		infoTable.setDefaultEditor(Object.class, null);
		infoTable.setRowHeight(23);
		infoTable.setModel(model);
		model.addColumn("key");
		model.addColumn("value");
		model.addColumn("warning");
		model.addColumn("highlight");
		model.addColumn("hide");
		infoTable.getColumnModel().getColumn(0).setPreferredWidth(186);
		infoTable.getColumnModel().getColumn(1).setPreferredWidth(100);
		infoTable.getColumnModel().getColumn(2).setPreferredWidth(15);
		infoTable.getColumnModel().getColumn(3).setPreferredWidth(15);
		infoTable.getColumnModel().getColumn(4).setPreferredWidth(15);

		int selectedInterval = headerInterval.getSelectedIndex();
		for (Statistic stats : Ramolos.getInstance().getStatisticManager().getStatistics().values()) {
			model.addRow(new Object[] {
				stats.getName(), 
				stats.getFormattedValues(selectedInterval),
				iconEmpty,
				iconEmpty,
				iconEmpty
			});
		}

		sorter = new TableRowSorter<DefaultTableModel>(model);
		infoTable.setRowSorter(sorter);
		updateFilter();

		infoTable.addMouseMotionListener(tableHoverListener);
		infoTable.addMouseListener(tableClickListener);
		
		JScrollPane listScroller = new JScrollPane(infoTable);
		listScroller.getVerticalScrollBar().setUnitIncrement(14);
		listScroller.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));	

		JPanel footer = new JPanel();
		footer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		footer.setLayout(new GridLayout(1, 2));

		footerLableDate = new JLabel("YYYY-MM-DD HH:mm (-)", JLabel.CENTER);
		footerLableDate.setFont(Window.getCustomFont());
		
		JButton playerStatsButton = new JButton("Spielerstats");
		playerStatsButton.setFont(Window.getCustomFont());
		playerStatsButton.addActionListener(e -> {
			playerStatsWindow = new PlayerStatsWindow();
		});
		
		footer.add(footerLableDate);
		footer.add(playerStatsButton);

		add(header);
		add(listScroller);
		add(footer);
	}

	private void initListener() {
		tableHoverListener = new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				StatisticManager sm = Ramolos.getInstance().getStatisticManager();
				int hoveredRow = infoTable.rowAtPoint(e.getPoint());
				boolean hidden = selectedInterval == Statistic.HIDDEN;
				Statistic stat = sm.getStatisticByFilteredIndex(hoveredRow, hidden);
				for (int i = 0; i < infoTable.getModel().getRowCount(); i++) {
					model.setValueAt(i == stat.getIndex() && stat.getType().isAxeInfluenced() ? iconWarning : iconEmpty, i, 2);
					model.setValueAt(i == stat.getIndex() ? iconHighlight : iconEmpty, i, 3);
					model.setValueAt(i == stat.getIndex() ? 
						(selectedInterval == Statistic.HIDDEN ? iconShow : iconHide)
						: iconEmpty, i, 4);
				}
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				mouseMoved(e);
			}
		};

		tableClickListener = new MouseListener() {
			@Override
			public void mousePressed(MouseEvent e) {
				StatisticManager sm = Ramolos.getInstance().getStatisticManager();
				int column = infoTable.columnAtPoint(e.getPoint());
				int hoveredRow = infoTable.rowAtPoint(e.getPoint());
				boolean hidden = selectedInterval == Statistic.HIDDEN;
				int targetIndex = sm.getStatisticByFilteredIndex(hoveredRow, hidden).getIndex();
				for (Statistic s : Ramolos.getInstance().getStatisticManager().getStatistics().values()) {
					if (s.getIndex() != targetIndex) continue;
					if (column == 2 && s.getType().isAxeInfluenced()) showAxeWarning();
					if (column == 3) s.toggleHighlight();
					else if (column == 4) s.toggleVisibility();
				}
				tableHoverListener.mouseMoved(e);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				for (int i = 0; i < infoTable.getModel().getRowCount(); i++) {
					model.setValueAt(iconEmpty, i, 2);
					model.setValueAt(iconEmpty, i, 3);
					model.setValueAt(iconEmpty, i, 4);
				}
			}

			@Override public void mouseClicked(MouseEvent e) { }
			@Override public void mouseReleased(MouseEvent e) { }
			@Override public void mouseEntered(MouseEvent e) { }
		};

		headerFocusListener = new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (headerSearch.getText().equals("Filter")) {
					headerSearch.setText("");
					headerSearch.setForeground(Color.decode("#BBBBBB"));
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				if (headerSearch.getText().isEmpty()) {
					headerSearch.setForeground(Color.decode("#878787"));
					headerSearch.setText("Filter");
				}
			}
		};

		searchChangeListener = new DocumentListener() {
			public void onChange() {
				filterText = headerSearch.getText().toLowerCase();
				updateFilter();
			}

			@Override public void changedUpdate(DocumentEvent e) { onChange(); }
			@Override public void removeUpdate(DocumentEvent e) { onChange(); }
			@Override public void insertUpdate(DocumentEvent e) { onChange(); }
		};
	}

	private void updateFilter() {
		boolean hidden = selectedInterval == Statistic.HIDDEN;
		if (filterText.equals("filter")) filterText = "";

		String regex = String.format("^<html><span style='color: #.{6}'>(?%s‎)(?i).*%s.*<\\/span><\\/html>",
		hidden ? "=" : "!", filterText);
		sorter.setRowFilter(RowFilter.regexFilter(regex, 0));

	}

	public void updateFooter(String time, int count) {
		footerLableDate.setText(String.format("%s (%d)", time, count));
	}

	public void updateTableCell(int line, String name, String value) {
		model.setValueAt(name, line, 0);
		model.setValueAt(value, line, 1);
		updateFilter();
	}

	public int getSelectedInterval() {
		return selectedInterval == Statistic.HIDDEN ? Statistic.INTERVAL_ALLTIME : selectedInterval;
	}

	public PlayerStatsWindow getPlayerStatsWindow() {
		return playerStatsWindow;
	}

	private void showAxeWarning() {
		Ramolos.getInstance().getWindow().showSimpleInfoDialog("Dieser Wert ist " +
			"abhängig von der Axt-Tode-Statistik. Die Axt-Tode-Statistik " +
			"aktualisiert sich verzögert und kann, je nach Spielweise, nicht " +
			"alle Axt-Tode erfassen. Dieser Wert ist daher ebenfalls mit einer " +
			"Ungenauigkeit behaftet.", "Axt Ungenauigkeit");
	}
}

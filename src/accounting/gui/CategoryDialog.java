/***************************************************************************
    begin........: February 2012
    copyright....: Sebastian Fedrau
    email........: sebastian.fedrau@gmail.com
 ***************************************************************************/

/***************************************************************************
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License 3 as published by
    the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    General Public License for more details.
 ***************************************************************************/
package accounting.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.*;
import org.picocontainer.PicoContainer;
import accounting.*;
import accounting.application.*;
import accounting.data.*;

public class CategoryDialog extends ADialog implements ActionListener
{
	public static final int RESULT_DELETE = 0;
	public static final int RESULT_OK = 1;
	public static final int SHOW_INCOME = 1;
	public static final int SHOW_EXPENSE = 2;

	private static final long serialVersionUID = 2527776778200979645L;
	private Factory factory;
	private Container contentPane;
    private JList<Category> listEntries;
    private JButton buttonAddExpense;
    private JButton buttonAddIncome;
    private JButton buttonRename;
    private JButton buttonDelete;
    private JButton buttonOk;
    private int flags = SHOW_INCOME | SHOW_EXPENSE;
	private int result = RESULT_DELETE;
	private Vector<ICategoryListener> listener = new Vector<ICategoryListener>();
	private Translation translation;

	public CategoryDialog(JFrame parent, Category preSelected, int flags)
	{
		super(parent, "Categories");

		this.flags = flags;
		populateCategories();

		if(preSelected != null)
		{
			listEntries.setSelectedValue(preSelected, true);
		}
	}

	public int getResult()
	{
		return result;
	}

	public Category getSelectedCategory()
	{
		List<Category> entries = listEntries.getSelectedValuesList();

		if(entries != null && entries.size() >= 1)
		{
			return entries.get(0);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	protected void initialize()
	{
		PicoContainer pico;
		BorderLayout borderLayout;
		JPanel panelContent;
		JPanel panel;

		translation = new Translation();

		setTitle(translation.translate(getTitle()));

		try
		{
			pico = Injection.getContainer();
			factory = pico.getComponent(Factory.class);
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		contentPane = getContentPane();

		setResizable(false);
		borderLayout = new BorderLayout();
		borderLayout.setVgap(5);
		contentPane.setLayout(borderLayout);
		
		// content panel:
		panelContent = new JPanel();
		borderLayout = new BorderLayout();
		borderLayout.setVgap(5);
		panelContent.setLayout(borderLayout);
		panelContent.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		contentPane.add(panelContent, BorderLayout.CENTER);

		// list:
		listEntries = new JList<Category>();
		listEntries.setModel(new GenericListModel<Category>());
		listEntries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listEntries.registerKeyboardAction(new ActionListener()
		{	
			@Override
			public void actionPerformed(ActionEvent event)
			{
				deleteSelectedCategory();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		panelContent.add(new JScrollPane(listEntries), BorderLayout.CENTER);
		
		// action buttons:
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelContent.add(panel, BorderLayout.SOUTH);
		
		buttonAddExpense = new JButton("Add expense");
		buttonAddExpense.setName("buttonAddExpense");
		buttonAddExpense.addActionListener(this);
		panel.add(buttonAddExpense);

		buttonAddIncome = new JButton("Add income");
		buttonAddIncome.setName("buttonAddIncome");
		buttonAddIncome.addActionListener(this);
		panel.add(buttonAddIncome);

		buttonRename = new JButton("Rename");
		buttonRename.setName("buttonRename");
		buttonRename.addActionListener(this);
		panel.add(buttonRename);
		
		buttonDelete = new JButton("Delete");
		buttonDelete.setName("buttonDelete");
		buttonDelete.addActionListener(this);
		panel.add(buttonDelete);
		
		// button panel:
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		contentPane.add(panel, BorderLayout.SOUTH);
		
		buttonOk = new JButton("Ok");
		buttonOk.setName("buttonOk");
		buttonOk.addActionListener(this);
		panel.add(buttonOk);
	}

	private void addCategory(boolean expenditure)
	{
		@SuppressWarnings("unchecked")
		GenericListModel<Category> model = (GenericListModel<Category>)listEntries.getModel();
		String name;
		Category category;

		name = JOptionPane.showInputDialog(this, translation.translate("Please enter a name for the new category:"), translation.translate("new category"));

		if (name != null && !name.isEmpty())
		{
			try
			{
				category = factory.createCategory(name, expenditure);
				model.add(category);
				model.sort();
				listEntries.setSelectedValue(category, true);
			}
			catch(ProviderException e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, translation.translate("Couldn't create category, please try again."), translation.translate("Warning"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void renameSelectedCategory()
	{
		@SuppressWarnings("unchecked")
		GenericListModel<Category> model = (GenericListModel<Category>)listEntries.getModel();
		Category category;
		String name;

		if((category = getSelectedCategory()) != null)
		{
			name = JOptionPane.showInputDialog(this, translation.translate("Please enter a name for the category:"), category.getName());

			if (name != null && !name.isEmpty())
			{
				try
				{
					category.setName(name);
					category.save();

					for(ICategoryListener listener : this.listener)
					{
						listener.categoryChanged(new EntityEvent(category));
					}
					
					model.sort();
					listEntries.setSelectedValue(category, true);
				}
				catch(AttributeException e)
				{
					JOptionPane.showMessageDialog(this, translation.translate("Invalid category name, please check the entered text and try again."), translation.translate("Warning"), JOptionPane.ERROR_MESSAGE);
				}
				catch(ProviderException e)
				{
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, translation.translate("An internal failure occured, please try again."), translation.translate("Warning"), JOptionPane.ERROR_MESSAGE);
				}
			}    
		}
	}

	private void deleteSelectedCategory()
	{
		Category category;

		try
		{
			if((category = getSelectedCategory()) != null)
			{
				if(factory.countCategories((category.isExpenditure())) == 1)
				{
					JOptionPane.showMessageDialog(this, translation.translate("You cannot delete the last category from the selected type."), translation.translate("Warning"), JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if(JOptionPane.showConfirmDialog(this, translation.translate("Do you really want to delete the selected category?")) == JOptionPane.YES_OPTION)
				{
					category.delete();
				}
			}
		}
		catch(ReferenceException e)
		{
			JOptionPane.showMessageDialog(this, translation.translate("Cannot delete category, object is still in use."), translation.translate("Warning"), JOptionPane.ERROR_MESSAGE);
		}
		catch(ProviderException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, translation.translate("An internal failure occured, please try again."), translation.translate("Warning"), JOptionPane.ERROR_MESSAGE);
		}
	}

	private void populateCategories()
	{
		@SuppressWarnings("unchecked")
		GenericListModel<Category> model = (GenericListModel<Category>)listEntries.getModel();

		try
		{
			if((flags & SHOW_INCOME) != 0)
			{
				for(Category category : factory.getCategories(false))
				{	
					model.add(category);
				}
			}
			else
			{
				buttonAddIncome.setVisible(false);
			}

			if((flags & SHOW_EXPENSE) != 0)
			{
				for(Category category : factory.getCategories(true))
				{	
					model.add(category);
				}
			}
			else
			{
				buttonAddExpense.setVisible(false);
			}

			model.sort();
		}
		catch (ProviderException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() instanceof JButton)
		{
			if(event.getSource().equals(buttonAddExpense))
			{
				addCategory(true);
			}
			else if(event.getSource().equals(buttonAddIncome))
			{
				addCategory(false);
			}
			else if(event.getSource().equals(buttonRename))
			{
				renameSelectedCategory();
			}
			else if(event.getSource().equals(buttonDelete))
			{
				deleteSelectedCategory();
			}
			else if(event.getSource().equals(buttonOk))
			{
				result = RESULT_OK;
				close();
			}
		}
	}

	public void addCategoryListener(ICategoryListener listener)
	{
		this.listener.add(listener);
	}

	public void removeCategoryListener(ICategoryListener listener)
	{
		this.listener.remove(listener);
	}
}

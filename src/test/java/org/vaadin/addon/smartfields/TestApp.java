package org.vaadin.addon.smartfields;


import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.gson.GsonBuilder;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class TestApp extends UI {
	
	public static void main(String[] args) throws Exception {
		startInEmbeddedJetty();
	}

	public static Server startInEmbeddedJetty() throws Exception {
		Server server = new Server(8888);
		ServletContextHandler handler = new ServletContextHandler(
				ServletContextHandler.SESSIONS);
		handler.addServlet(Servlet.class, "/*");
		server.setHandler(handler);
		server.start();
		return server;
	}

	// mapping used to if demo war is built from these sources
	@WebServlet(urlPatterns = "/*")
    @VaadinServletConfiguration(productionMode = false, ui = TestApp.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {

		Form form = new Form();
		SmartFieldFactory fieldFactory = new SmartFieldFactory();
		

		SmartFieldContext smartFieldContext = SmartFieldContext.get();
		smartFieldContext.setFieldType(TestBean.class, List.class, "persons", PopupEditableCollectionField.class);
		
		smartFieldContext.setEditableProperties(Person.class, "firstName", "lastName", "gender", "age");
		smartFieldContext.setVisibleProperties(Person.class, "firstName", "lastName");
		
		form.setFormFieldFactory(fieldFactory);
		final TestBean bean = new TestBean();
		form.setItemDataSource(new BeanItem<TestBean>(bean));
		
		
		Button button = new Button("Show state");
		button.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				String json = new GsonBuilder().setPrettyPrinting().create().toJson(bean);
				Notification.show("<small><pre>" + json + "</pre></small>");
				System.err.print(json);
			}
		});
		setContent(new VerticalLayout(form,button));
		
	}

}

package org.vaadin.addon.smartfields;


import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.gson.GsonBuilder;
import com.vaadin.Application;
import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.Window;

public class TestApp extends Application {
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
	public static class Servlet extends AbstractApplicationServlet {
		@Override
		protected Application getNewApplication(HttpServletRequest request)
				throws ServletException {
			return new TestApp();
		}

		@Override
		protected Class<? extends Application> getApplicationClass()
				throws ClassNotFoundException {
			return TestApp.class;
		}
	}

	@Override
	public void init() {
		
		Form form = new Form();
		SmartFieldFactory fieldFactory = new SmartFieldFactory();
		
		fieldFactory.getContext().setFieldType(TestBean.class, List.class, "persons", PopupEditableCollectionField.class);
		
		form.setFormFieldFactory(fieldFactory);
		final TestBean bean = new TestBean();
		form.setItemDataSource(new BeanItem<TestBean>(bean));
		
		Window window = new Window();
		window.addComponent(form);
		
		setMainWindow(window);
		
		Button button = new Button("Show state");
		button.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				String json = new GsonBuilder().setPrettyPrinting().create().toJson(bean);
				event.getButton().getWindow().showNotification(json);
				System.err.print(json);
			}
		});
		window.addComponent(button);
		
	}

}

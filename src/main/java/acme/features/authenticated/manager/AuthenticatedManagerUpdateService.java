
package acme.features.authenticated.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.accounts.Authenticated;
import acme.client.data.accounts.Principal;
import acme.client.data.models.Dataset;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractService;
import acme.components.SpamDetector;
import acme.entities.configuration.Configuration;
import acme.roles.Manager;

@Service
public class AuthenticatedManagerUpdateService extends AbstractService<Authenticated, Manager> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedManagerRepository repository;

	// AbstractService interface ----------------------------------------------ç


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Manager object;
		Principal principal;
		int userAccountId;

		principal = super.getRequest().getPrincipal();
		userAccountId = principal.getAccountId();
		object = this.repository.findOneManagerByUserAccountId(userAccountId);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Manager object) {
		assert object != null;

		super.bind(object, "degree", "overview", "certifications", "link");
	}

	@Override
	public void validate(final Manager object) {
		assert object != null;

		Configuration config = this.repository.findConfiguration();
		String spamTerms = config.getSpamTerms();
		Double spamThreshold = config.getSpamThreshold();
		SpamDetector spamHelper = new SpamDetector(spamTerms, spamThreshold);

		if (!super.getBuffer().getErrors().hasErrors("degree"))
			super.state(!spamHelper.isSpam(object.getDegree()), "degree", "authenticated.manager.form.error.spam");

		if (!super.getBuffer().getErrors().hasErrors("overview"))
			super.state(!spamHelper.isSpam(object.getOverview()), "overview", "authenticated.manager.form.error.spam");

		if (!super.getBuffer().getErrors().hasErrors("certifications"))
			super.state(!spamHelper.isSpam(object.getCertifications()), "certifications", "authenticated.manager.form.error.spam");
	}

	@Override
	public void perform(final Manager object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final Manager object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "degree", "overview", "certifications", "link");
		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}

package org.example.services;

import org.example.model.Case;
import org.example.model.Donor;

public interface IObserver {
    void notifyCaseUpdated(Case caseUpdated) throws Exception;

    void notifyDonorAdded(Donor donor) throws Exception;
}

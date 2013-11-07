---------------------
AgreementMaker-UIGlue
---------------------

This bundle is meant to be a connection (or glue) between bundles 
that should not depend on the UI bundle but need to be present in the UI.

For example, the BatchMode bundle should not depend on the UI bundle,
but we would like to be able to execute a batch mode from the AgreementMaker UI.
The pieces to make that happen would be stored in this bundle.

Some bundles inherently depend on the UI, but the preferred practice is to avoid
depending on the UI bundle unless that bundle specifically needs UI classes.

Another example would be the matching algorithms.  They should not depend on the 
UI, but the currently do because of the MatcherParametersPanel.  This dependency
should be refactored out (and it may be in the future).

							-- Cosmin, 11/7/2013
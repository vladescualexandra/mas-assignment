package ro.ase.ism.mas_assignment.async;

public interface Callback<R> {
    void runResultOnUIThread(R result);
}